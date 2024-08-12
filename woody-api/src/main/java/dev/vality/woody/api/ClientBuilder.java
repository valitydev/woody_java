package dev.vality.woody.api;

import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.generator.IdGenerator;
import io.opentelemetry.sdk.resources.Resource;

import java.net.URI;

public interface ClientBuilder {
    ClientBuilder withAddress(URI address);

    ClientBuilder withOtelResource(String otelResource);

    ClientBuilder withEventListener(ClientEventListener listener);

    ClientBuilder withIdGenerator(IdGenerator generator);

    ClientBuilder withNetworkTimeout(int timeout);

    URI getAddress();

    ClientEventListener getEventListener();

    IdGenerator getIdGenerator();

    int getNetworkTimeout();

    <T> T build(Class<T> iface);
}
