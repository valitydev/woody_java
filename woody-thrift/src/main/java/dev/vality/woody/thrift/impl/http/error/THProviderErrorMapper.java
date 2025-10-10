package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.*;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.Metadata;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.thrift.impl.http.TErrorType;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;
import dev.vality.woody.thrift.impl.http.THResponseInfo;
import dev.vality.woody.thrift.impl.http.interceptor.THRequestInterceptionException;
import dev.vality.woody.thrift.impl.http.transport.TTransportErrorType;
import org.apache.hc.core5.http.impl.EnglishReasonPhraseCatalog;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class THProviderErrorMapper implements WErrorMapper {

    public static final Function<Object, String> THRIFT_TRANSPORT_ERROR_REASON_FUNC = obj -> "thrift transport error";
    public static final Function<Object, String> THRIFT_PROTOCOL_ERROR_REASON_FUNC = obj -> "thrift protocol error";
    public static final Function<Object, String> UNKNOWN_PROVIDER_ERROR_REASON_FUNC =
            msg -> "unknown provider error: " + msg;
    public static final Function<Object, String> UNKNOWN_CALL_REASON_FUNC = callName -> "unknown method: " + callName;
    public static final UnaryOperator<String> BAD_CONTENT_TYPE_REASON_FUNC = cType -> "content type wrong/missing";
    public static final UnaryOperator<String> RPC_ID_HEADER_MISSING_REASON_FUNC = header -> header + " missing";
    public static final UnaryOperator<String> BAD_HEADER_REASON_FUNC = header -> "bad header: " + header;
    public static final UnaryOperator<String> BAD_REQUEST_TYPE_REASON_FUNC = rewMethod -> "http method wrong";
    private static final String UNKNOWN_ERROR_MESSAGE = "internal thrift application error";

    public static WErrorDefinition createErrorDefinition(THResponseInfo responseInfo, Supplier invalidErrClass) {
        WErrorDefinition errorDefinition = null;
        WErrorSource errorSource = null;
        int status = responseInfo.getStatus();
        if (status == 200) {
            if (WErrorType.getValueByKey(responseInfo.getErrClass()) == WErrorType.BUSINESS_ERROR) {
                errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
                errorDefinition.setErrorType(WErrorType.BUSINESS_ERROR);
                errorSource = WErrorSource.INTERNAL;
                errorDefinition.setErrorReason(responseInfo.getErrReason());
                errorDefinition.setErrorName(responseInfo.getErrReason());
            }
        } else if (status == 503) {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(WErrorType.UNAVAILABLE_RESULT);
            errorSource = WErrorSource.INTERNAL;
            errorDefinition.setErrorReason(responseInfo.getErrReason());
        } else if (status == 504) {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(WErrorType.UNDEFINED_RESULT);
            errorSource = WErrorSource.INTERNAL;
            errorDefinition.setErrorReason(responseInfo.getErrReason());
        } else if (status == 502) {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(Optional.ofNullable(WErrorType.getValueByKey(responseInfo.getErrClass()))
                    .orElse(WErrorType.UNEXPECTED_ERROR));
            errorSource = WErrorSource.EXTERNAL;
            errorDefinition.setErrorReason(responseInfo.getErrReason());
            if (errorDefinition.getErrorType() == WErrorType.BUSINESS_ERROR) {
                invalidErrClass.get();
            }
        } else {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(WErrorType.UNEXPECTED_ERROR);
            errorSource = WErrorSource.INTERNAL;
            errorDefinition.setErrorReason(responseInfo.getErrReason());
        }
        if (errorDefinition != null) {
            if (errorSource != null) {
                errorDefinition.setErrorSource(errorSource);
            }
            int messageStatus = status;
            if (status >= 500
                    && (responseInfo.getErrClass() == null || responseInfo.getErrClass().isEmpty())
                    && responseInfo.getErrReason() != null) {
                messageStatus = 400;
            }
            String message = responseInfo.getMessage();
            if (messageStatus >= 400 && messageStatus < 500) {
                message = defaultReasonPhrase(messageStatus);
            }
            if (message == null || message.isEmpty()) {
                message = defaultReasonPhrase(messageStatus);
            }
            errorDefinition.setErrorMessage(message);
        }
        return errorDefinition;
    }

    public static THResponseInfo getResponseInfo(ContextSpan contextSpan) {
        WErrorDefinition errorDefinition =
                ContextUtils.getMetadataValue(contextSpan, WErrorDefinition.class, MetadataProperties.ERROR_DEFINITION);
        int status;
        String errClass = null;
        String errReason = null;
        Throwable interceptionThrowable = ContextUtils.getInterceptionError(contextSpan);
        THRequestInterceptionException interceptionError =
                interceptionThrowable instanceof THRequestInterceptionException
                        ? (THRequestInterceptionException) interceptionThrowable
                        : null;
        if (errorDefinition == null) {
            status = 200;
        } else {
            switch (errorDefinition.getErrorType()) {
                case BUSINESS_ERROR:
                    status = 200;
                    errClass = WErrorType.BUSINESS_ERROR.getKey();
                    break;
                case PROVIDER_ERROR:
                    errClass = WErrorType.UNEXPECTED_ERROR.getKey();
                    TErrorType tErrorType = ContextUtils.getMetadataValue(contextSpan, TErrorType.class,
                            THMetadataProperties.TH_ERROR_TYPE);
                    tErrorType = tErrorType == null ? TErrorType.UNKNOWN : tErrorType;
                    boolean isRequest =
                            !contextSpan.getMetadata().containsKey(MetadataProperties.CALL_REQUEST_PROCESSED_FLAG);
                    if (isRequest) {
                        switch (tErrorType) {
                            case PROTOCOL:
                                status = 400;
                                break;
                            case TRANSPORT:
                                TTransportErrorType tTransportErrorType =
                                        ContextUtils.getMetadataValue(contextSpan, TTransportErrorType.class,
                                                THMetadataProperties.TH_ERROR_SUBTYPE);
                                status = mapTransportErrorStatus(tTransportErrorType);
                                break;
                            case UNKNOWN_CALL:
                            case UNKNOWN:
                            default:
                                status = 400;
                        }
                    } else {
                        status = errorDefinition.getGenerationSource() == WErrorSource.INTERNAL ? 500 : 502;
                    }
                    break;
                case UNAVAILABLE_RESULT:
                    status = errorDefinition.getGenerationSource() == WErrorSource.INTERNAL ? 503 : 502;
                    errClass = WErrorType.UNAVAILABLE_RESULT.getKey();
                    break;
                case UNDEFINED_RESULT:
                    status = errorDefinition.getGenerationSource() == WErrorSource.INTERNAL ? 504 : 502;
                    errClass = WErrorType.UNDEFINED_RESULT.getKey();
                    break;
                case UNEXPECTED_ERROR:
                default:
                    status = errorDefinition.getGenerationSource() == WErrorSource.INTERNAL ? 500 : 502;
                    errClass = WErrorType.UNEXPECTED_ERROR.getKey();
                    break;
            }
            errReason = errorDefinition.getErrorReason();

        }
        if (interceptionError != null) {
            status = mapTransportErrorStatus(interceptionError.getErrorType());
            errClass = null;
        } else {
            Throwable callError = ContextUtils.getCallError(contextSpan);
            if (callError instanceof THRequestInterceptionException) {
                status = mapTransportErrorStatus(((THRequestInterceptionException) callError).getErrorType());
                errClass = null;
            }
        }
        return new THResponseInfo(status, errClass, errReason);
    }

    private static int mapTransportErrorStatus(TTransportErrorType errorType) {
        TTransportErrorType ttType = errorType == null ? TTransportErrorType.UNKNOWN : errorType;
        switch (ttType) {
            case BAD_REQUEST_TYPE:
                return 405;
            case BAD_CONTENT_TYPE:
                return 415;
            case BAD_TRACE_HEADER:
            case BAD_HEADER:
            case UNKNOWN:
            default:
                return 400;
        }
    }

    @Override
    public WErrorDefinition mapToDef(Throwable t, ContextSpan contextSpan) {
        if (isThriftError(t) || isInternalTransportErr(t)) {
            WErrorDefinition errorDefinition = ContextUtils.getErrorDefinition(contextSpan);
            //If transport interceptor has already read error def data, this data has more priority than thrift error
            //Woody error def always overrides other errors on provider level (except woody transport error)
            if (errorDefinition != null && !isInternalTransportErr(t)) {
                return errorDefinition;
            }
            return createDefFromWrappedError(contextSpan.getMetadata(), t);
        }
        return null;
    }

    @Override
    public Exception mapToError(WErrorDefinition errorDefinition, ContextSpan contextSpan) {
        if (errorDefinition.getErrorType() == WErrorType.PROVIDER_ERROR) {
            return new WRuntimeException(errorDefinition);
        }
        return null;
    }

    private boolean isThriftError(Throwable t) {
        return t instanceof TException;
    }

    private boolean isInternalTransportErr(Throwable t) {
        return t instanceof THRequestInterceptionException;
    }

    private WErrorDefinition createDefFromWrappedError(Metadata metadata, Throwable err) {
        WErrorDefinition existingDefinition = metadata.getValue(MetadataProperties.ERROR_DEFINITION);
        if (existingDefinition == null) {
            THResponseInfo storedResponse = metadata.getValue(THMetadataProperties.TH_RESPONSE_INFO);
            if (storedResponse != null) {
                existingDefinition = createErrorDefinition(storedResponse, () -> null);
                if (existingDefinition != null) {
                    metadata.putValue(MetadataProperties.ERROR_DEFINITION, existingDefinition);
                }
            }
        }
        if (existingDefinition != null && !(err instanceof THRequestInterceptionException)) {
            return existingDefinition;
        }
        WErrorType errorType = WErrorType.PROVIDER_ERROR;
        TErrorType tErrorType;
        String errReason;
        WErrorSource generationSource = WErrorSource.INTERNAL;
        WErrorSource errorSource = generationSource;
        if (err instanceof TApplicationException) {
            TApplicationException appError = (TApplicationException) err;
            switch (appError.getType()) {
                case TApplicationException.PROTOCOL_ERROR:
                    tErrorType = TErrorType.PROTOCOL;
                    errReason = THRIFT_PROTOCOL_ERROR_REASON_FUNC.apply(appError);
                    break;
                case TApplicationException.UNKNOWN_METHOD:
                    tErrorType = TErrorType.UNKNOWN_CALL;
                    errReason = UNKNOWN_CALL_REASON_FUNC.apply(metadata.getValue(MetadataProperties.CALL_NAME));
                    break;
                case TApplicationException.INTERNAL_ERROR:
                default:
                    tErrorType = TErrorType.UNKNOWN;
                    errReason = UNKNOWN_PROVIDER_ERROR_REASON_FUNC.apply(err.getMessage());
                    break;
            }
        } else if (err instanceof TProtocolException) {
            tErrorType = TErrorType.PROTOCOL;
            errReason = THRIFT_PROTOCOL_ERROR_REASON_FUNC.apply(err);
        } else if (err instanceof TTransportException) {
            tErrorType = TErrorType.TRANSPORT;
            errReason = THRIFT_TRANSPORT_ERROR_REASON_FUNC.apply(err);
            Integer httpStatus = metadata.getValue(THMetadataProperties.TH_RESPONSE_STATUS);
            if (httpStatus != null && httpStatus >= 400 && httpStatus < 500) {
                generationSource = WErrorSource.EXTERNAL;
                errorSource = generationSource;
            } else if (httpStatus == null && isNoPayloadTransportError(err)) {
                generationSource = WErrorSource.EXTERNAL;
                errorSource = generationSource;
            }
        } else if (err instanceof THRequestInterceptionException) {
            tErrorType = TErrorType.TRANSPORT;
            TTransportErrorType ttErrType = ((THRequestInterceptionException) err).getErrorType();
            ttErrType = ttErrType == null ? TTransportErrorType.UNKNOWN : ttErrType;

            metadata.putValue(THMetadataProperties.TH_ERROR_SUBTYPE, ttErrType);
            boolean isClientContext = TraceContext.getCurrentTraceData().isClient();
            generationSource = isClientContext ? WErrorSource.INTERNAL : WErrorSource.EXTERNAL;
            errorSource = generationSource;
            String reason = String.valueOf(((THRequestInterceptionException) err).getReason());
            switch (ttErrType) {
                case BAD_CONTENT_TYPE:
                    errReason = BAD_CONTENT_TYPE_REASON_FUNC.apply(reason);
                    break;
                case BAD_REQUEST_TYPE:
                    errReason = BAD_REQUEST_TYPE_REASON_FUNC.apply(reason);
                    break;
                case BAD_TRACE_HEADER:
                    errReason = RPC_ID_HEADER_MISSING_REASON_FUNC.apply(reason);
                    break;
                case BAD_HEADER:
                    errReason = BAD_HEADER_REASON_FUNC.apply(reason);
                    break;
                case UNKNOWN:
                default:
                    errReason = THRIFT_TRANSPORT_ERROR_REASON_FUNC.apply(reason);
                    break;
            }

        } else {
            tErrorType = TErrorType.UNKNOWN;
            errReason = UNKNOWN_ERROR_MESSAGE;
        }
        WErrorDefinition errorDefinition = new WErrorDefinition(generationSource);
        errorDefinition.setErrorType(errorType);
        errorDefinition.setErrorSource(errorSource);
        errorDefinition.setErrorReason(errReason);
        errorDefinition.setErrorName(err.getClass().getSimpleName());
        errorDefinition.setErrorMessage(err.getMessage());

        metadata.putValue(THMetadataProperties.TH_ERROR_TYPE, tErrorType);
        return errorDefinition;
    }

    private static boolean isNoPayloadTransportError(Throwable err) {
        if (!(err instanceof TTransportException)) {
            return false;
        }
        String message = err.getMessage();
        if (message == null) {
            return false;
        }
        String normalized = message.trim();
        return normalized.equals("No more data available.")
                || normalized.contains("HTTP response code:")
                || normalized.contains("HTTP Response code:");
    }

    private static String defaultReasonPhrase(int status) {
        String reason = EnglishReasonPhraseCatalog.INSTANCE.getReason(status, Locale.ENGLISH);
        return reason != null ? reason : "HTTP Status " + status;
    }

}
