package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.*;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;

public class THSystemErrorMapper implements WErrorMapper {

    @Override
    public WErrorDefinition mapToDef(Throwable t, ContextSpan contextSpan) {
        if (t instanceof WRuntimeException) {
            return ((WRuntimeException) t).getErrorDefinition();
        } else {
            WErrorDefinition errorDefinition = ContextUtils.getErrorDefinition(contextSpan);
            if (errorDefinition != null) {
                WErrorType errorType = errorDefinition.getErrorType();
                if (errorType == WErrorType.UNEXPECTED_ERROR ||
                        errorType == WErrorType.UNAVAILABLE_RESULT || errorType == WErrorType.UNDEFINED_RESULT) {
                    return errorDefinition;
                }
            }
            return null;
        }
    }

    @Override
    public Exception mapToError(WErrorDefinition errorDefinition, ContextSpan contextSpan) {
        WErrorType errorType = errorDefinition.getErrorType();
        if (errorType != null) {
            switch (errorType) {
                case UNAVAILABLE_RESULT:
                    return new WUnavailableResultException(errorDefinition);
                case UNDEFINED_RESULT:
                    return new WUndefinedResultException(errorDefinition);
                case UNEXPECTED_ERROR:
                    return new WRuntimeException(errorDefinition);
                default:
                    return null;
            }
        }
        return null;
    }
}
