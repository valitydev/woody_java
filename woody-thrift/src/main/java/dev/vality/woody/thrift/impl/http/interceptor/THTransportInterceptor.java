package dev.vality.woody.thrift.impl.http.interceptor;

import dev.vality.woody.api.interceptor.CommonInterceptor;
import dev.vality.woody.api.interceptor.ext.ExtendableInterceptor;
import dev.vality.woody.api.interceptor.ext.ExtensionBundle;
import dev.vality.woody.api.interceptor.ext.ExtensionContext;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.thrift.impl.http.interceptor.ext.THCExtensionContext;
import dev.vality.woody.thrift.impl.http.interceptor.ext.THSExtensionContext;
import dev.vality.woody.thrift.impl.http.interceptor.ext.TransportExtensionBundles;

import java.util.Collections;
import java.util.List;

public class THTransportInterceptor extends ExtendableInterceptor implements CommonInterceptor {
    private final boolean isClient;

    public THTransportInterceptor(boolean isClient, boolean isRequest) {
        this(Collections.emptyList(), isClient, isRequest);
    }

    public THTransportInterceptor(List<ExtensionBundle> extensionBundles, boolean isClient, boolean isRequest) {
        super(TransportExtensionBundles::getExtensions, extensionBundles, isClient, isRequest);
        this.isClient = isClient;
    }

    @Override
    protected ExtensionContext createContext(TraceData traceData, Object providerContext, Object[] contextParams) {
        return isClient ? new THCExtensionContext(traceData, providerContext, contextParams) :
                new THSExtensionContext(traceData, providerContext, contextParams);
    }
}
