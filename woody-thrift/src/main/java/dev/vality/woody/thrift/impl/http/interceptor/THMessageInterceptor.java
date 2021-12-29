package dev.vality.woody.thrift.impl.http.interceptor;

import dev.vality.woody.api.interceptor.CommonInterceptor;
import dev.vality.woody.api.interceptor.ext.ExtendableInterceptor;
import dev.vality.woody.api.interceptor.ext.ExtensionBundle;
import dev.vality.woody.api.interceptor.ext.ExtensionContext;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.thrift.impl.http.interceptor.ext.MessageExtensionBundles;
import dev.vality.woody.thrift.impl.http.interceptor.ext.THCExtensionContext;
import dev.vality.woody.thrift.impl.http.interceptor.ext.THSExtensionContext;

import java.util.Collections;
import java.util.List;

public class THMessageInterceptor extends ExtendableInterceptor implements CommonInterceptor {
    private final boolean isClient;

    public THMessageInterceptor(boolean isClient, boolean isRequest) {
        this(Collections.emptyList(), isClient, isRequest);
    }

    public THMessageInterceptor(List<ExtensionBundle> extensionBundles, boolean isClient, boolean isRequest) {
        super(MessageExtensionBundles::getExtensions, extensionBundles, isClient, isRequest);
        this.isClient = isClient;
    }

    @Override
    protected ExtensionContext createContext(TraceData traceData, Object providerContext, Object[] contextParams) {
        return isClient ? new THCExtensionContext(traceData, providerContext, contextParams) :
                new THSExtensionContext(traceData, providerContext, contextParams);
    }
}
