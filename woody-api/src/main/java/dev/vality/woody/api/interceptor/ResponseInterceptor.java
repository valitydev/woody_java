package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.trace.TraceData;

public interface ResponseInterceptor extends Interceptor {
    /**
     * @return true - if response is successfully intercepted and ready for further processing;
     *      false - if interception failed and processing must be switched to response err handling
     */
    default boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        return intercept(traceData, providerContext, contextParams);
    }

}
