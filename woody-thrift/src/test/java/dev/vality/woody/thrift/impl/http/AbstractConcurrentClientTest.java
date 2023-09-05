package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.event.ServiceEventListener;
import dev.vality.woody.api.generator.IdGenerator;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.thrift.impl.http.event.ClientEventListenerImpl;
import dev.vality.woody.thrift.impl.http.event.ClientEventLogListener;
import dev.vality.woody.thrift.impl.http.event.ServiceEventListenerImpl;
import dev.vality.woody.thrift.impl.http.event.ServiceEventLogListener;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;

import jakarta.servlet.Servlet;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConcurrentClientTest extends AbstractTest {
    final AtomicInteger clientCalls = new AtomicInteger(0);
    final AtomicInteger serverAccepts = new AtomicInteger(0);
    protected long runTime = 5000;
    protected int threadsNum = 8;
    protected ServiceEventListener serviceEventStub = new ServiceEventListenerImpl();
    protected ServiceEventListener serviceEventLogger = new ServiceEventLogListener();
    protected ClientEventListener clientEventStub = new ClientEventListenerImpl();
    protected ClientEventListener clientEventLogger = new ClientEventLogListener();

    @Test
    public void testPool() throws InterruptedException {
        Servlet servlet = createThriftRPCService(OwnerServiceSrv.Iface.class, new OwnerServiceStub() {
            @Override
            public Owner getOwner(int id) throws TException {
                serverAccepts.incrementAndGet();
                return super.getOwner(id);
            }

            @Override
            public void setOwner(Owner owner) throws TException {
                serverAccepts.incrementAndGet();
                super.setOwner(owner);
            }
        }, serviceEventLogger);
        addServlet(servlet, "/load");
        OwnerServiceSrv.Iface client =
                createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(), clientEventLogger,
                        getUrlString() + "/load");

        ExecutorService executor = Executors.newFixedThreadPool(threadsNum);

        Collection<Callable> callableCollection = Collections.nCopies(threadsNum, () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    client.getOwner(0);
                    clientCalls.incrementAndGet();
                    client.setOwner(new Owner(0, ""));
                    clientCalls.incrementAndGet();
                    //Thread.sleep(100);

                } catch (Exception e) {
                    if (!(e instanceof InterruptedException)) {
                        e.printStackTrace();
                    }
                }
            }
            return null;

        });

        callableCollection.stream().forEach((callable -> executor.submit(callable)));


        Thread watcher = new Thread(() -> {
            int lastCVal = 0;
            int lastSVal = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    int currCVal = clientCalls.get();
                    int currSVal = serverAccepts.get();
                    System.out.println("C Op/sec:" + (currCVal - lastCVal));
                    System.out.println("S Op/sec:" + (currSVal - lastSVal));
                    lastCVal = currCVal;
                    lastSVal = currSVal;
                } catch (InterruptedException e) {
                    return;
                }

            }
        });
        watcher.start();

        Thread.sleep(runTime);

        executor.shutdownNow();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        watcher.interrupt();

        Assert.assertEquals(clientCalls.get(), serverAccepts.get());

    }

    @Override
    protected <T> Servlet createThriftRPCService(Class<T> iface, T handler, ServiceEventListener eventListener) {
        return super.createThriftRPCService(iface, handler, serviceEventStub);
    }

    @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:ModifierOrder"})
    abstract protected <T> T createThriftRPCClient(Class<T> iface, IdGenerator idGenerator,
                                                   ClientEventListener eventListener, String url);
}
