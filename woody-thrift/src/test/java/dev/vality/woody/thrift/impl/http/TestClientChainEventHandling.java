package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.event.ClientEventType;
import dev.vality.woody.api.flow.error.WRuntimeException;
import dev.vality.woody.api.flow.error.WUndefinedResultException;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import dev.vality.woody.thrift.impl.http.event.THClientEvent;
import org.apache.thrift.TException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestClientChainEventHandling extends AbstractTest {

    OwnerServiceSrv.Iface client1 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                    assertFalse(thClientEvent.isSuccessfulCall());
                    assertEquals(new Integer(502), thClientEvent.getThriftResponseStatus());
                }
            });
    OwnerServiceSrv.Iface client2 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                    assertFalse(thClientEvent.isSuccessfulCall());
                    assertEquals(new Integer(504), thClientEvent.getThriftResponseStatus());
                }
            });
    OwnerServiceSrv.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getOwner(int id) throws TException {
            switch (id) {
                case 0:
                    throw new RuntimeException("err");
                case 10:
                    return client2.getOwner(20);
                case 20:
                    throw new WUndefinedResultException("err");
                case 1:
                    return new Owner(1, "name1");
                default:
                    return new Owner(-1, "default");
            }
        }

        @Override
        public Owner getErrOwner(int id) throws test_error {
            throw new test_error(id);
        }
    };

    @Test
    public void testUndefinedResultError() throws TException {
        addServlet(createMutableTServlet(OwnerServiceSrv.Iface.class, handler), "/");
        try {
            client1.getOwner(10);
            fail();
        } catch (WRuntimeException e) {
            e.printStackTrace();
        }
    }

}
