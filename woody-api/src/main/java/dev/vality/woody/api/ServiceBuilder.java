package dev.vality.woody.api;

import dev.vality.woody.api.event.ServiceEventListener;

public interface ServiceBuilder<Srv> {
    ServiceBuilder withEventListener(ServiceEventListener listener);

    ServiceEventListener getEventListener();

    <T> Srv build(Class<T> iface, T serviceHandler);
}
