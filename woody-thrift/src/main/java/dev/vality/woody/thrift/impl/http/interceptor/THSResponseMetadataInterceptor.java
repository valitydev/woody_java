package dev.vality.woody.thrift.impl.http.interceptor;

import dev.vality.woody.api.interceptor.ResponseInterceptor;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.thrift.impl.http.error.THErrorMapProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class THSResponseMetadataInterceptor implements ResponseInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(THSResponseMetadataInterceptor.class);

    private final THErrorMapProcessor errorMapProcessor;

    public THSResponseMetadataInterceptor(THErrorMapProcessor errorMapProcessor) {
        this.errorMapProcessor = errorMapProcessor;
    }

    @Override
    public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("Intercept response metadata");
        errorMapProcessor.processMapToDef(traceData);
        return true;
    }
}
