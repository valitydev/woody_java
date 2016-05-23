package com.rbkmoney.woody.thrift.impl.http;

import com.rbkmoney.woody.api.event.ClientEventListener;
import com.rbkmoney.woody.api.event.CompositeClientEventListener;
import com.rbkmoney.woody.api.event.CompositeServiceEventListener;
import com.rbkmoney.woody.rpc.Owner;
import com.rbkmoney.woody.rpc.OwnerService;
import com.rbkmoney.woody.rpc.test_error;
import com.rbkmoney.woody.thrift.impl.http.event.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.Servlet;

import static java.lang.System.out;

/**
 * Created by vpankrashkin on 12.05.16.
 */

public class TestChildRequests extends AbstractTest {

    ServiceEventListenerImpl serviceEventListener = new ServiceEventListenerImpl();
    ClientEventListener clientEventLogListener = new CompositeClientEventListener(
            new ClientEventLogListener(),
            new HttpClientEventLogListener()
    );

    OwnerService.Iface client1 = createThriftRPCClient(OwnerService.Iface.class, new IdGeneratorStub(), clientEventLogListener, getUrlString("/rpc"));
    OwnerService.Iface client2 = createThriftRPCClient(OwnerService.Iface.class, new IdGeneratorStub(), clientEventLogListener, getUrlString("/rpc"));
    OwnerService.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getErrOwner(int id) throws TException, test_error {
            switch (id) {
                case 0:
                    Owner owner = client2.getOwner(0);
                    client2.setOwnerOneway(owner);
                    return client2.getOwner(10);
                case 200:
                    throw new test_error(200);
                case 500:
                    throw new RuntimeException("Test");
                default:
                    return super.getErrOwner(id);
            }
        }
    };

    Servlet servlet = createThrftRPCService(OwnerService.Iface.class, handler, new CompositeServiceEventListener<>(
            new ServiceEventLogListener(),
            new HttpServiceEventLogListener()
    ));

    @Before
    public void before() {
        addServlet(servlet, "/rpc");
    }

    @Test
    public void testEventOrder() throws TException {
        out.println("Root call>");
        Assert.assertEquals(new Owner(10, "10"), client1.getErrOwner(0));
        out.println("<");

        out.println("Root call>");
        try {
            client1.getErrOwner(200);
            Assert.fail();
        } catch (test_error e) {
        }
        out.println("<");
        out.println("Root call>");
        try {
            client1.getErrOwner(500);
        } catch (TTransportException e) {
        }
        out.println("<");


    }

}
