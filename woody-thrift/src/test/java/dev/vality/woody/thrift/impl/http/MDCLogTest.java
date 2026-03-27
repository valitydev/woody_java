package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.MDCUtils;
import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.event.CompositeClientEventListener;
import dev.vality.woody.api.event.CompositeServiceEventListener;
import dev.vality.woody.api.event.ServiceEventListener;
import dev.vality.woody.api.flow.error.*;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.thrift.impl.http.event.THClientEvent;
import dev.vality.woody.thrift.impl.http.event.THServiceEvent;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.Servlet;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class MDCLogTest extends AbstractTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    ClientEventListener clientEventListener = (ClientEventListener<THClientEvent>) event -> {
        assertNotNull(MDC.get(MDCUtils.SPAN_ID));
        assertNotNull(MDC.get(MDCUtils.TRACE_ID));
        assertNotNull(MDC.get(MDCUtils.PARENT_ID));

        assertEquals(MDC.get(MDCUtils.SPAN_ID), event.getSpanId());
        assertEquals(MDC.get(MDCUtils.TRACE_ID), event.getTraceId());
        assertEquals(MDC.get(MDCUtils.PARENT_ID), event.getParentId());
    };

    ServiceEventListener serviceEventListener = (ServiceEventListener<THServiceEvent>) event -> {
        assertNotNull(MDC.get(MDCUtils.SPAN_ID));
        assertNotNull(MDC.get(MDCUtils.TRACE_ID));
        assertNotNull(MDC.get(MDCUtils.PARENT_ID));

        assertEquals(MDC.get(MDCUtils.SPAN_ID), event.getSpanId());
        assertEquals(MDC.get(MDCUtils.TRACE_ID), event.getTraceId());
        assertEquals(MDC.get(MDCUtils.PARENT_ID), event.getParentId());
    };

    OwnerServiceSrv.Iface client1 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            new CompositeClientEventListener(clientEventListener), getUrlString("/rpc"));
    OwnerServiceSrv.Iface client2 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            new CompositeClientEventListener(clientEventListener), getUrlString("/rpc"));
    OwnerServiceSrv.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getErrOwner(int id) throws TException {
            switch (id) {
                case 0:
                    Owner owner = client2.getOwner(0);
                    client2.setOwnerOneway(owner);
                    return client2.getOwner(10);
                case 200:
                    throw new test_error(200);
                case 201:
                    return client2.getErrOwner(202);
                case 202:
                    throw new WUnavailableResultException("Fake WUnavailableResultException");
                case 203:
                    return client2.getErrOwner(204);
                case 204:
                    throw new WUndefinedResultException("Fake WUndefinedResultException");
                case 500:
                    throw new RuntimeException("Test");
                default:
                    return super.getErrOwner(id);
            }
        }
    };

    Servlet servlet = createThriftRPCService(OwnerServiceSrv.Iface.class, handler,
            new CompositeServiceEventListener(serviceEventListener));

    @Before
    public void before() {
        addServlet(servlet, "/rpc");
    }

    @Test
    public void testMDCContext() throws TException {
        out.println("Root call>");
        Assert.assertEquals(new Owner(10, "10"), client1.getErrOwner(0));
        out.println("<");

        out.println("Root call>");
        try {
            client1.getErrOwner(200);
            Assert.fail();
        } catch (test_error ignored) {
            //ignore
        }

        out.println("Root call>");
        try {
            client1.getErrOwner(201);
            Assert.fail();
        } catch (WUnavailableResultException e) {
            assertEquals(e.getErrorDefinition().getGenerationSource(), WErrorSource.EXTERNAL);
            assertEquals(e.getErrorDefinition().getErrorSource(), WErrorSource.EXTERNAL);
            assertEquals(e.getErrorDefinition().getErrorType(), WErrorType.UNAVAILABLE_RESULT);
        }

        out.println("Root call>");
        try {
            client1.getErrOwner(203);
            Assert.fail();
        } catch (WUndefinedResultException e) {
            assertEquals(e.getErrorDefinition().getGenerationSource(), WErrorSource.EXTERNAL);
            assertEquals(e.getErrorDefinition().getErrorSource(), WErrorSource.EXTERNAL);
            assertEquals(e.getErrorDefinition().getErrorType(), WErrorType.UNDEFINED_RESULT);
        }

        out.println("<");
        out.println("Root call>");
        try {
            client1.getErrOwner(500);
            fail();
        } catch (WRuntimeException e) {
            assertEquals(e.getErrorDefinition().getGenerationSource(), WErrorSource.EXTERNAL);
            assertEquals(e.getErrorDefinition().getErrorSource(), WErrorSource.INTERNAL);
            assertEquals(e.getErrorDefinition().getErrorType(), WErrorType.UNEXPECTED_ERROR);
        }
        out.println("<");
    }
}
