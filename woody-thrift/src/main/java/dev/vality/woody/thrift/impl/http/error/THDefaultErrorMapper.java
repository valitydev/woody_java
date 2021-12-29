package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.*;
import dev.vality.woody.api.trace.ContextSpan;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class THDefaultErrorMapper implements WErrorMapper {
    @Override
    public WErrorDefinition mapToDef(Throwable t, ContextSpan contextSpan) {
        WErrorDefinition errorDefinition = new WErrorDefinition(WErrorSource.INTERNAL);
        errorDefinition.setErrorType(WErrorType.UNEXPECTED_ERROR);
        errorDefinition.setErrorSource(WErrorSource.INTERNAL);
        errorDefinition.setErrorReason(t.getClass().getSimpleName() + ":" + t.getMessage());
        errorDefinition.setErrorName(t.getClass().getSimpleName());
        errorDefinition.setErrorMessage(t.getMessage());
        return errorDefinition;
    }

    @Override
    public Exception mapToError(WErrorDefinition errorDefinition, ContextSpan contextSpan) {
        if (errorDefinition.getErrorType() != WErrorType.BUSINESS_ERROR) {
            return new WRuntimeException(errorDefinition);
        }
        return null;
    }
}
