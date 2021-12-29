package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.generator.IdGenerator;
import org.junit.Ignore;

import java.net.URI;
import java.net.URISyntaxException;

@Ignore
public class TestTHPooledClientBuilder extends AbstractConcurrentClientTest {

    protected <T> T createThriftRPCClient(Class<T> iface, IdGenerator idGenerator, ClientEventListener eventListener,
                                          String url) {
        try {
            THPooledClientBuilder clientBuilder = new THPooledClientBuilder();
            clientBuilder.withAddress(new URI(url));
            clientBuilder.withIdGenerator(idGenerator);
            clientBuilder.withEventListener(clientEventStub);
            return clientBuilder.build(iface);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
