package dev.vality.woody.api;

import dev.vality.woody.api.event.ServiceEventListener;
import io.opentelemetry.sdk.resources.Resource;

public interface ServiceBuilder<SrvT> {
    ServiceBuilder withEventListener(ServiceEventListener listener);

    ServiceBuilder withOtelResource(Resource resource);

    ServiceEventListener getEventListener();

    <T> SrvT build(Class<T> iface, T serviceHandler);
}
