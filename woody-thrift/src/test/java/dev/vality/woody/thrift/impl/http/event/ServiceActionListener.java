package dev.vality.woody.thrift.impl.http.event;

import dev.vality.woody.api.event.ServiceEvent;

public interface ServiceActionListener {
    ServiceEvent callHandler(ServiceEvent event);

    ServiceEvent handlerResult(ServiceEvent event);

    ServiceEvent serviceReceive(ServiceEvent event);

    ServiceEvent serviceResult(ServiceEvent event);

    ServiceEvent error(ServiceEvent event);

    ServiceEvent undefined(ServiceEvent event);
}
