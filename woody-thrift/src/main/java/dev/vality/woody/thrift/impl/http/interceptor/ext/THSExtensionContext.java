package dev.vality.woody.thrift.impl.http.interceptor.ext;

import dev.vality.woody.api.interceptor.ext.ExtensionContext;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class THSExtensionContext extends ExtensionContext {
    public THSExtensionContext(TraceData traceData, Object providerContext, Object[] contextParameters) {
        super(traceData, providerContext, contextParameters);
    }

    public HttpServletRequest getProviderRequest() {
        Object providerContext = getProviderContext();
        if (providerContext instanceof HttpServletRequest) {
            return (HttpServletRequest) providerContext;
        }
        throw new IllegalArgumentException("Unknown type:" + providerContext.getClass());
    }

    public HttpServletResponse getProviderResponse() {
        HttpServletResponse response = null;
        Object providerContext = getProviderContext();
        if (providerContext instanceof HttpServletResponse) {
            response = (HttpServletResponse) providerContext;
        }
        if (response == null) {
            response = ContextUtils.getContextValue(HttpServletResponse.class, getContextParameters(), 0);
        }
        if (response == null) {
            response = ContextUtils.getMetadataValue(getTraceData().getServiceSpan(), HttpServletResponse.class,
                    THMetadataProperties.TH_TRANSPORT_RESPONSE);
        }

        if (response == null) {
            throw new IllegalArgumentException("Unknown type:" + providerContext.getClass() + "|" +
                    ContextUtils.getContextValue(Object.class, getContextParameters(), 0));
        }
        return response;
    }

    public void setResponseHeader(String key, String value) {
        if (key != null && value != null) {
            getProviderResponse().setHeader(key, value);
        }
    }
}
