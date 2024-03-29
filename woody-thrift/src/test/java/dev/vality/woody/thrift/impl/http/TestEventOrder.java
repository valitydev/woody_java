package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import dev.vality.woody.thrift.impl.http.event.ClientActionListener;
import dev.vality.woody.thrift.impl.http.event.ClientEventListenerImpl;
import dev.vality.woody.thrift.impl.http.event.ServiceActionListener;
import dev.vality.woody.thrift.impl.http.event.ServiceEventListenerImpl;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.Servlet;

import static org.easymock.EasyMock.*;

public class TestEventOrder extends AbstractTest {

    ClientEventListenerImpl clientEventListener = new ClientEventListenerImpl();
    ServiceEventListenerImpl serviceEventListener = new ServiceEventListenerImpl();
    OwnerServiceSrv.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getErrOwner(int id) throws TException {
            if (id == 500) {
                throw new RuntimeException("Test");
            }
            return super.getErrOwner(id);
        }
    };

    Servlet servlet = createThriftRPCService(OwnerServiceSrv.Iface.class, handler, serviceEventListener);

    OwnerServiceSrv.Iface client =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(), clientEventListener,
                    getUrlString("/rpc"));

    @Before
    public void before() {
        addServlet(servlet, "/rpc");
    }

    @Test
    public void testEventOrder() throws TException {

        ClientActionListener clientActionListener = createStrictMock(ClientActionListener.class);
        expect(clientActionListener.callService(anyObject())).andReturn(null);
        expect(clientActionListener.clientSend(anyObject())).andReturn(null);
        expect(clientActionListener.clientReceive(anyObject())).andReturn(null);
        expect(clientActionListener.serviceResult(anyObject())).andReturn(null);
        replay(clientActionListener);

        ServiceActionListener serviceEventActionListener = createStrictMock(ServiceActionListener.class);
        expect(serviceEventActionListener.serviceReceive(anyObject())).andReturn(null);
        expect(serviceEventActionListener.callHandler(anyObject())).andReturn(null);
        expect(serviceEventActionListener.handlerResult(anyObject())).andReturn(null);
        expect(serviceEventActionListener.serviceResult(anyObject())).andReturn(null);
        replay(serviceEventActionListener);

        clientEventListener.setEventActionListener(clientActionListener);
        serviceEventListener.setEventActionListener(serviceEventActionListener);

        client.getOwner(0);
        System.out.println("Target client:" + client);
        verify(clientActionListener);
    }

    @Test
    public void testOneWayEventOrder() throws TException {

        ClientActionListener clientActionListener = createStrictMock(ClientActionListener.class);
        expect(clientActionListener.callService(anyObject())).andReturn(null);
        expect(clientActionListener.clientSend(anyObject())).andReturn(null);
        expect(clientActionListener.clientReceive(anyObject())).andReturn(null);
        expect(clientActionListener.serviceResult(anyObject())).andReturn(null);
        replay(clientActionListener);

        ServiceActionListener serviceEventActionListener = createStrictMock(ServiceActionListener.class);
        expect(serviceEventActionListener.serviceReceive(anyObject())).andReturn(null);
        expect(serviceEventActionListener.callHandler(anyObject())).andReturn(null);
        expect(serviceEventActionListener.handlerResult(anyObject())).andReturn(null);
        expect(serviceEventActionListener.serviceResult(anyObject())).andReturn(null);
        replay(serviceEventActionListener);

        clientEventListener.setEventActionListener(clientActionListener);
        serviceEventListener.setEventActionListener(serviceEventActionListener);

        client.setOwnerOneway(new Owner(0, ""));

        verify(clientActionListener);
    }

    @Test
    public void testKnownErrEventOrder() throws TException {

        ClientActionListener clientActionListener = createStrictMock(ClientActionListener.class);
        expect(clientActionListener.callService(anyObject())).andReturn(null);
        expect(clientActionListener.clientSend(anyObject())).andReturn(null);
        expect(clientActionListener.clientReceive(anyObject())).andReturn(null);
        expect(clientActionListener.error(anyObject())).andReturn(null);
        replay(clientActionListener);

        ServiceActionListener serviceEventActionListener = createStrictMock(ServiceActionListener.class);
        expect(serviceEventActionListener.serviceReceive(anyObject())).andReturn(null);
        expect(serviceEventActionListener.callHandler(anyObject())).andReturn(null);
        expect(serviceEventActionListener.error(anyObject())).andReturn(null);
        expect(serviceEventActionListener.serviceResult(anyObject())).andReturn(null);
        replay(serviceEventActionListener);

        clientEventListener.setEventActionListener(clientActionListener);
        serviceEventListener.setEventActionListener(serviceEventActionListener);

        try {
            client.getErrOwner(1);
            Assert.fail("Exception should be here");
        } catch (test_error e) {
            Assert.assertEquals(1, e.getId());
        }

        verify(clientActionListener);
    }

    @Test
    public void testUnknownErrEventOrder() throws TException {

        ClientActionListener clientActionListener = createStrictMock(ClientActionListener.class);
        expect(clientActionListener.callService(anyObject())).andReturn(null);
        expect(clientActionListener.clientSend(anyObject())).andReturn(null);
        expect(clientActionListener.clientReceive(anyObject())).andReturn(null);
        expect(clientActionListener.error(anyObject())).andReturn(null);
        replay(clientActionListener);

        ServiceActionListener serviceEventActionListener = createStrictMock(ServiceActionListener.class);
        expect(serviceEventActionListener.serviceReceive(anyObject())).andReturn(null);
        expect(serviceEventActionListener.callHandler(anyObject())).andReturn(null);
        expect(serviceEventActionListener.error(anyObject())).andReturn(null);
        expect(serviceEventActionListener.serviceResult(anyObject())).andReturn(null);
        replay(serviceEventActionListener);

        clientEventListener.setEventActionListener(clientActionListener);
        serviceEventListener.setEventActionListener(serviceEventActionListener);

        try {
            client.getErrOwner(500);
            Assert.fail("Exception should be here");
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(clientActionListener);
    }
}
