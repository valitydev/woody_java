package dev.vality.woody.api.flow.error;

import dev.vality.woody.api.trace.ContextSpan;

public interface WErrorMapper {

    WErrorDefinition mapToDef(Throwable t, ContextSpan contextSpan);

    Exception mapToError(WErrorDefinition eDefinition, ContextSpan contextSpan);

}
