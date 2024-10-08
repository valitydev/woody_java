package dev.vality.woody.api.transport;

import dev.vality.woody.api.event.ClientEventType;
import dev.vality.woody.api.event.ServiceEventType;
import dev.vality.woody.api.interceptor.CommonInterceptor;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.api.trace.TraceData;
import io.opentelemetry.api.trace.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportEventInterceptor implements CommonInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TransportEventInterceptor.class);

    private final Runnable reqListener;
    private final Runnable respListener;
    private final Runnable errListener;

    public TransportEventInterceptor(Runnable reqListener, Runnable respListener, Runnable errListener) {
        this.reqListener = reqListener != null ? reqListener : () -> {
        };
        this.respListener = respListener != null ? respListener : () -> {
        };
        this.errListener = errListener != null ? errListener : () -> {
        };
    }

    @Override
    public boolean interceptRequest(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("Intercept request transportEvent");
        traceData.getActiveSpan().getMetadata().putValue(MetadataProperties.EVENT_TYPE,
                traceData.isClient() ? ClientEventType.CLIENT_SEND : ServiceEventType.SERVICE_RECEIVE);
        traceData.getOtelSpan()
                .setStatus(StatusCode.OK)
                .addEvent(traceData.isClient() ? ClientEventType.CLIENT_SEND.name() :
                        ServiceEventType.SERVICE_RECEIVE.name());
        reqListener.run();
        return true;
    }

    @Override
    public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("Intercept response transportEvent");
        traceData.getActiveSpan().getMetadata().putValue(MetadataProperties.EVENT_TYPE,
                traceData.isClient() ? ClientEventType.CLIENT_RECEIVE : ServiceEventType.SERVICE_RESULT);
        traceData.getOtelSpan()
                .setStatus(StatusCode.OK)
                .addEvent(traceData.isClient() ? ClientEventType.CLIENT_RECEIVE.name() :
                        ServiceEventType.SERVICE_RESULT.name());
        respListener.run();
        return true;
    }

    @Override
    public boolean interceptError(TraceData traceData, Throwable t, boolean isClient) {
        LOG.trace("Intercept error transportEvent");
        traceData.getOtelSpan()
                .setStatus(StatusCode.ERROR)
                .addEvent("ERROR");
        errListener.run();
        return (CommonInterceptor.super.interceptError(traceData, t, isClient));
    }
}
