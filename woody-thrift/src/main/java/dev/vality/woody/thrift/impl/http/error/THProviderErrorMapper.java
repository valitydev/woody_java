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
        WErrorDefinition definition = buildDefinition(responseInfo, invalidErrClass);
        if (definition == null) {
            return null;
        }
        definition.setErrorMessage(resolveMessage(responseInfo));
        return definition;
    }

    private static WErrorDefinition buildDefinition(THResponseInfo responseInfo, Supplier invalidErrClass) {
        int status = responseInfo.getStatus();
        switch (status) {
            case 200:
                return businessErrorDefinition(responseInfo);
            case 503:
                return availabilityDefinition(WErrorType.UNAVAILABLE_RESULT, responseInfo);
            case 504:
                return availabilityDefinition(WErrorType.UNDEFINED_RESULT, responseInfo);
            case 502:
                return gatewayErrorDefinition(responseInfo, invalidErrClass);
            default:
                return unexpectedDefinition(responseInfo);
        }
    }

    private static WErrorDefinition businessErrorDefinition(THResponseInfo responseInfo) {
        if (WErrorType.getValueByKey(responseInfo.getErrClass()) != WErrorType.BUSINESS_ERROR) {
            return null;
        }
        WErrorDefinition definition = baseDefinition(WErrorType.BUSINESS_ERROR, responseInfo.getErrReason(),
                WErrorSource.INTERNAL);
        definition.setErrorName(responseInfo.getErrReason());
        return definition;
    }

    private static WErrorDefinition availabilityDefinition(WErrorType type, THResponseInfo responseInfo) {
        return baseDefinition(type, responseInfo.getErrReason(), WErrorSource.INTERNAL);
    }

    private static WErrorDefinition gatewayErrorDefinition(THResponseInfo responseInfo, Supplier invalidErrClass) {
        WErrorType errorType = Optional.ofNullable(WErrorType.getValueByKey(responseInfo.getErrClass()))
                .orElse(WErrorType.UNEXPECTED_ERROR);
        if (errorType == WErrorType.BUSINESS_ERROR) {
            invalidErrClass.get();
        }
        return baseDefinition(errorType, responseInfo.getErrReason(), WErrorSource.EXTERNAL);
    }

    private static WErrorDefinition unexpectedDefinition(THResponseInfo responseInfo) {
        return baseDefinition(WErrorType.UNEXPECTED_ERROR, responseInfo.getErrReason(), WErrorSource.INTERNAL);
    }

    private static WErrorDefinition baseDefinition(WErrorType type, String reason, WErrorSource errorSource) {
        WErrorDefinition definition = new WErrorDefinition(WErrorSource.EXTERNAL);
        definition.setErrorType(type);
        if (errorSource != null) {
            definition.setErrorSource(errorSource);
        }
        definition.setErrorReason(reason);
        return definition;
    }

    private static String resolveMessage(THResponseInfo responseInfo) {
        int status = responseInfo.getStatus();
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
        return message;
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
                            case TRANSPORT:
                                TTransportErrorType tTransportErrorType =
                                        ContextUtils.getMetadataValue(contextSpan, TTransportErrorType.class,
                                                THMetadataProperties.TH_ERROR_SUBTYPE);
                                status = mapTransportErrorStatus(tTransportErrorType);
                                break;
                            case PROTOCOL:
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
        WErrorDefinition existingDefinition = resolveExistingDefinition(metadata, err);
        if (existingDefinition != null) {
            return existingDefinition;
        }

        ErrorAttributes attributes = buildErrorAttributes(metadata, err);
        WErrorDefinition errorDefinition = new WErrorDefinition(attributes.getSource());
        errorDefinition.setErrorType(WErrorType.PROVIDER_ERROR);
        errorDefinition.setErrorSource(attributes.getSource());
        errorDefinition.setErrorReason(attributes.getReason());
        errorDefinition.setErrorName(err.getClass().getSimpleName());
        errorDefinition.setErrorMessage(err.getMessage());

        metadata.putValue(THMetadataProperties.TH_ERROR_TYPE, attributes.getTransportType());
        return errorDefinition;
    }

    private WErrorDefinition resolveExistingDefinition(Metadata metadata, Throwable err) {
        WErrorDefinition definition = metadata.getValue(MetadataProperties.ERROR_DEFINITION);
        if (definition == null) {
            definition = restoreDefinitionFromResponse(metadata);
        }
        if (definition != null && !(err instanceof THRequestInterceptionException)) {
            return definition;
        }
        return null;
    }

    private WErrorDefinition restoreDefinitionFromResponse(Metadata metadata) {
        THResponseInfo storedResponse = metadata.getValue(THMetadataProperties.TH_RESPONSE_INFO);
        if (storedResponse == null) {
            return null;
        }
        WErrorDefinition definition = createErrorDefinition(storedResponse, () -> null);
        if (definition != null) {
            metadata.putValue(MetadataProperties.ERROR_DEFINITION, definition);
        }
        return definition;
    }

    private ErrorAttributes buildErrorAttributes(Metadata metadata, Throwable err) {
        if (err instanceof TApplicationException) {
            return fromApplicationException(metadata, (TApplicationException) err);
        }
        if (err instanceof TProtocolException) {
            return attributes(TErrorType.PROTOCOL, THRIFT_PROTOCOL_ERROR_REASON_FUNC.apply(err),
                    WErrorSource.INTERNAL);
        }
        if (err instanceof TTransportException) {
            return fromTransportException(metadata, (TTransportException) err);
        }
        if (err instanceof THRequestInterceptionException) {
            return fromInterceptionException(metadata, (THRequestInterceptionException) err);
        }
        return attributes(TErrorType.UNKNOWN, UNKNOWN_ERROR_MESSAGE, WErrorSource.INTERNAL);
    }

    private ErrorAttributes fromApplicationException(Metadata metadata, TApplicationException appError) {
        switch (appError.getType()) {
            case TApplicationException.PROTOCOL_ERROR:
                return attributes(TErrorType.PROTOCOL, THRIFT_PROTOCOL_ERROR_REASON_FUNC.apply(appError),
                        WErrorSource.INTERNAL);
            case TApplicationException.UNKNOWN_METHOD:
                return attributes(TErrorType.UNKNOWN_CALL,
                        UNKNOWN_CALL_REASON_FUNC.apply(metadata.getValue(MetadataProperties.CALL_NAME)),
                        WErrorSource.INTERNAL);
            case TApplicationException.INTERNAL_ERROR:
            default:
                return attributes(TErrorType.UNKNOWN,
                        UNKNOWN_PROVIDER_ERROR_REASON_FUNC.apply(appError.getMessage()), WErrorSource.INTERNAL);
        }
    }

    private ErrorAttributes fromTransportException(Metadata metadata, TTransportException transportError) {
        Integer httpStatus = metadata.getValue(THMetadataProperties.TH_RESPONSE_STATUS);
        boolean isExternal = httpStatus != null && httpStatus >= 400 && httpStatus < 500;
        if (!isExternal && httpStatus == null && isNoPayloadTransportError(transportError)) {
            isExternal = true;
        }
        WErrorSource source = isExternal ? WErrorSource.EXTERNAL : WErrorSource.INTERNAL;
        return attributes(TErrorType.TRANSPORT, THRIFT_TRANSPORT_ERROR_REASON_FUNC.apply(transportError), source);
    }

    private ErrorAttributes fromInterceptionException(Metadata metadata,
                                                      THRequestInterceptionException interceptionError) {
        TTransportErrorType errorType = interceptionError.getErrorType();
        TTransportErrorType resolvedType = errorType == null ? TTransportErrorType.UNKNOWN : errorType;
        metadata.putValue(THMetadataProperties.TH_ERROR_SUBTYPE, resolvedType);

        WErrorSource source = TraceContext.getCurrentTraceData().isClient()
                ? WErrorSource.INTERNAL
                : WErrorSource.EXTERNAL;
        String reason = mapInterceptionReason(resolvedType, String.valueOf(interceptionError.getReason()));
        return attributes(TErrorType.TRANSPORT, reason, source);
    }

    private String mapInterceptionReason(TTransportErrorType errorType, String rawReason) {
        switch (errorType) {
            case BAD_CONTENT_TYPE:
                return BAD_CONTENT_TYPE_REASON_FUNC.apply(rawReason);
            case BAD_REQUEST_TYPE:
                return BAD_REQUEST_TYPE_REASON_FUNC.apply(rawReason);
            case BAD_TRACE_HEADER:
                return RPC_ID_HEADER_MISSING_REASON_FUNC.apply(rawReason);
            case BAD_HEADER:
                return BAD_HEADER_REASON_FUNC.apply(rawReason);
            case UNKNOWN:
            default:
                return THRIFT_TRANSPORT_ERROR_REASON_FUNC.apply(rawReason);
        }
    }

    private static ErrorAttributes attributes(TErrorType tErrorType, String reason, WErrorSource source) {
        return new ErrorAttributes(tErrorType, reason, source);
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

    private static final class ErrorAttributes {
        private final TErrorType transportType;
        private final String reason;
        private final WErrorSource source;

        private ErrorAttributes(TErrorType transportType, String reason, WErrorSource source) {
            this.transportType = transportType;
            this.reason = reason;
            this.source = source;
        }

        private TErrorType getTransportType() {
            return transportType;
        }

        private String getReason() {
            return reason;
        }

        private WErrorSource getSource() {
            return source;
        }
    }

    private static String defaultReasonPhrase(int status) {
        String reason = EnglishReasonPhraseCatalog.INSTANCE.getReason(status, Locale.ENGLISH);
        return reason != null ? reason : "HTTP Status " + status;
    }

}
