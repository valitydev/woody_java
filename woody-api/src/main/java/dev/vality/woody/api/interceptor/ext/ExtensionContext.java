package dev.vality.woody.api.interceptor.ext;

import dev.vality.woody.api.trace.TraceData;

public class ExtensionContext {
    private final TraceData traceData;
    private final Object providerContext;
    private final Object[] contextParameters;

    public ExtensionContext(TraceData traceData, Object providerContext, Object[] contextParameters) {
        this.traceData = traceData;
        this.providerContext = providerContext;
        this.contextParameters = contextParameters;
    }

    public TraceData getTraceData() {
        return traceData;
    }

    public Object getProviderContext() {
        return providerContext;
    }

    public Object[] getContextParameters() {
        return contextParameters;
    }

}
