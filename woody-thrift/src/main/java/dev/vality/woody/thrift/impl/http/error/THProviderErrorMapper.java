package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.*;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.Metadata;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.thrift.impl.http.TErrorType;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;
import dev.vality.woody.thrift.impl.http.THResponseInfo;
import dev.vality.woody.thrift.impl.http.interceptor.THRequestInterceptionException;
import dev.vality.woody.thrift.impl.http.transport.TTransportErrorType;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;

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
        int status = responseInfo.getStatus();
        if (status == 200) {
            if (WErrorType.getValueByKey(responseInfo.getErrClass()) == WErrorType.BUSINESS_ERROR) {
                errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
                errorDefinition.setErrorType(WErrorType.BUSINESS_ERROR);
                errorDefinition.setErrorSource(WErrorSource.INTERNAL);
                errorDefinition.setErrorReason(responseInfo.getErrReason());
                errorDefinition.setErrorName(responseInfo.getErrReason());
            }
        } else if (status == 503) {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(WErrorType.UNAVAILABLE_RESULT);
            errorDefinition.setErrorSource(WErrorSource.INTERNAL);
            errorDefinition.setErrorReason(responseInfo.getErrReason());
        } else if (status == 504) {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(WErrorType.UNDEFINED_RESULT);
            errorDefinition.setErrorSource(WErrorSource.INTERNAL);
            errorDefinition.setErrorReason(responseInfo.getErrReason());
        } else if (status == 502) {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(Optional.ofNullable(WErrorType.getValueByKey(responseInfo.getErrClass()))
                    .orElse(WErrorType.UNEXPECTED_ERROR));
            errorDefinition.setErrorSource(WErrorSource.EXTERNAL);
            errorDefinition.setErrorReason(responseInfo.getErrReason());
            if (errorDefinition.getErrorType() == WErrorType.BUSINESS_ERROR) {
                invalidErrClass.get();
            }
        } else {
            errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorType(WErrorType.UNEXPECTED_ERROR);
            errorDefinition.setErrorSource(WErrorSource.INTERNAL);
            errorDefinition.setErrorReason(responseInfo.getErrReason());
        }
        if (errorDefinition != null) {
            errorDefinition.setErrorMessage(responseInfo.getMessage());
        }
        return errorDefinition;
    }

    public static THResponseInfo getResponseInfo(ContextSpan contextSpan) {
        WErrorDefinition errorDefinition =
                ContextUtils.getMetadataValue(contextSpan, WErrorDefinition.class, MetadataProperties.ERROR_DEFINITION);
        int status;
        String errClass = null;
        String errReason = null;
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
                    if (errorDefinition.getGenerationSource() == WErrorSource.INTERNAL) {
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
                                    tTransportErrorType = tTransportErrorType == null ? TTransportErrorType.UNKNOWN :
                                            tTransportErrorType;
                                    switch (tTransportErrorType) {
                                        case BAD_REQUEST_TYPE:
                                            status = 405;
                                            break;
                                        case BAD_CONTENT_TYPE:
                                            status = 415;
                                            break;
                                        case BAD_TRACE_HEADER:
                                        case BAD_HEADER:
                                        case UNKNOWN:
                                        default:
                                            status = 400;
                                            break;
                                    }
                                    break;
                                case UNKNOWN_CALL:
                                case UNKNOWN:
                                default:
                                    status = 400;
                            }
                        } else {
                            status = 500;
                            errClass = WErrorType.UNEXPECTED_ERROR.getKey();
                        }
                    } else {
                        status = 500;
                        errClass = WErrorType.UNEXPECTED_ERROR.getKey();
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
        return new THResponseInfo(status, errClass, errReason);
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
        WErrorType errorType = WErrorType.PROVIDER_ERROR;
        TErrorType tErrorType;
        String errReason;
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
        } else if (err instanceof THRequestInterceptionException) {
            tErrorType = TErrorType.TRANSPORT;
            TTransportErrorType ttErrType = ((THRequestInterceptionException) err).getErrorType();
            String reason = String.valueOf(((THRequestInterceptionException) err).getReason());
            ttErrType = ttErrType == null ? TTransportErrorType.UNKNOWN : ttErrType;

            metadata.putValue(THMetadataProperties.TH_ERROR_SUBTYPE, ttErrType);
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
        WErrorDefinition errorDefinition = new WErrorDefinition(WErrorSource.INTERNAL);
        errorDefinition.setErrorType(errorType);
        errorDefinition.setErrorSource(WErrorSource.INTERNAL);
        errorDefinition.setErrorReason(errReason);
        errorDefinition.setErrorName(err.getClass().getSimpleName());
        errorDefinition.setErrorMessage(err.getMessage());

        metadata.putValue(THMetadataProperties.TH_ERROR_TYPE, tErrorType);
        return errorDefinition;
    }

}
