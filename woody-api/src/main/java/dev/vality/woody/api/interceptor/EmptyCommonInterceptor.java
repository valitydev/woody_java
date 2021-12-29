package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.trace.TraceData;

public class EmptyCommonInterceptor implements CommonInterceptor {

    @Override
    public boolean interceptRequest(TraceData traceData, Object providerContext, Object... contextParams) {
        return true;
    }

    @Override
    public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        return true;
    }
}
