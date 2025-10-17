package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MdcRefreshInterceptor implements CommonInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MdcRefreshInterceptor.class);

    @Override
    public boolean interceptRequest(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("MDC refresh on request phase");
        refresh(traceData);
        return true;
    }

    @Override
    public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("MDC refresh on response phase");
        refresh(traceData);
        return true;
    }

    public static void refresh(TraceData traceData) {
        if (traceData == null) {
            MDCUtils.removeTraceData();
            return;
        }
        ContextSpan activeSpan = traceData.getActiveSpan();
        if (activeSpan == null || !activeSpan.isFilled()) {
            LOG.trace("Active span is not filled; skipping MDC refresh");
            return;
        }
        MDCUtils.putTraceData(traceData, activeSpan);
    }

}