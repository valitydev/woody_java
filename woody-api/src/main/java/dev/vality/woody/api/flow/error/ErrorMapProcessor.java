package dev.vality.woody.api.flow.error;

import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;

import java.util.ArrayList;
import java.util.List;

public class ErrorMapProcessor {
    private final boolean isClient;
    private final List<WErrorMapper> mappers;

    public ErrorMapProcessor(boolean isClient, List<WErrorMapper> mappers) {
        this.isClient = isClient;
        this.mappers = new ArrayList<>(mappers);
    }

    public WErrorDefinition processMapToDef(TraceData traceData) {
        ContextSpan contextSpan = isClient ? traceData.getClientSpan() : traceData.getServiceSpan();
        Throwable t = ContextUtils.getCallError(contextSpan);
        WErrorDefinition errorDefinition = null;
        if (t != null) {
            for (int i = 0; errorDefinition == null && i < mappers.size(); ++i) {
                errorDefinition = mappers.get(i).mapToDef(t, contextSpan);
            }
        }
        return errorDefinition;
    }

    /**
     * @throws RuntimeException expected if any error occurs
     */
    public Exception processMapToError(TraceData traceData) {
        ContextSpan contextSpan = isClient ? traceData.getClientSpan() : traceData.getServiceSpan();
        WErrorDefinition errorDefinition = ContextUtils.getErrorDefinition(contextSpan);
        Exception ex = null;

        if (errorDefinition != null) {
            for (int i = 0; ex == null && i < mappers.size(); ++i) {
                ex = mappers.get(i).mapToError(errorDefinition, contextSpan);
            }
        }
        return ex;
    }
}
