package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.CallType;
import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.event.ClientEventType;
import dev.vality.woody.api.flow.error.*;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import dev.vality.woody.thrift.impl.http.event.THClientEvent;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import org.apache.thrift.TException;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TestClientEventHandling extends AbstractTest {

    OwnerServiceSrv.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getOwner(int id) throws TException {
            switch (id) {
                case 0:
                    throw new RuntimeException("err");
                case 10:
                    throw new WUnavailableResultException("err");
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
    public void testExpectedError() {
        addServlet(createMutableTServlet(OwnerServiceSrv.Iface.class, handler), "/");
        AtomicInteger order = new AtomicInteger();

        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    switch (thClientEvent.getEventType()) {
                        case CALL_SERVICE:
                            assertEquals(0, order.getAndIncrement());
                            assertArrayEquals(new Object[] {0}, thClientEvent.getCallArguments());
                            assertEquals("getErrOwner", thClientEvent.getCallName());
                            assertEquals(CallType.CALL, thClientEvent.getCallType());
                            assertEquals(TraceContext.NO_PARENT_ID, thClientEvent.getParentId());
                            assertNotNull(thClientEvent.getTraceId());
                            assertEquals(thClientEvent.getTraceId(), thClientEvent.getSpanId());
                            assertNull(thClientEvent.getEndpoint());
                            assertNotEquals(thClientEvent.getTimeStamp(), 0);
                            break;
                        case CLIENT_SEND:
                            assertEquals(1, order.getAndIncrement());
                            assertEquals(getUrlString(), thClientEvent.getEndpoint().getStringValue());
                            break;
                        case CLIENT_RECEIVE:
                            assertEquals(2, order.getAndIncrement());
                            assertEquals(new Integer(200), thClientEvent.getThriftResponseStatus());
                            assertEquals("OK", thClientEvent.getThriftResponseMessage());
                            break;
                        case SERVICE_RESULT:
                            fail("Should not be invoked on error");
                            break;
                        case ERROR:
                            assertEquals(3, order.getAndIncrement());
                            assertFalse(thClientEvent.isSuccessfulCall());
                            assertEquals(WErrorType.BUSINESS_ERROR, thClientEvent.getErrorDefinition().getErrorType());
                            assertEquals("test_error", thClientEvent.getErrorDefinition().getErrorName());
                            assertEquals("Error was generated outside of the client", WErrorSource.EXTERNAL,
                                    thClientEvent.getErrorDefinition().getGenerationSource());
                            assertEquals("This is internal service error", WErrorSource.INTERNAL,
                                    thClientEvent.getErrorDefinition().getErrorSource());
                            assertNull(thClientEvent.getThriftErrorType());
                            break;
                        default:
                            fail();
                    }


                });
        try {
            client.getErrOwner(0);
        } catch (TException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetOwnerOK() {
        addServlet(createMutableTServlet(OwnerServiceSrv.Iface.class, handler), "/");

        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    switch (thClientEvent.getEventType()) {
                        case CALL_SERVICE:
                            assertArrayEquals(new Object[] {1}, thClientEvent.getCallArguments());
                            assertEquals("getOwner", thClientEvent.getCallName());
                            assertEquals(CallType.CALL, thClientEvent.getCallType());
                            assertEquals(TraceContext.NO_PARENT_ID, thClientEvent.getParentId());
                            assertNotNull(thClientEvent.getTraceId());
                            assertEquals(thClientEvent.getTraceId(), thClientEvent.getSpanId());
                            assertNull(thClientEvent.getEndpoint());
                            assertNotEquals(thClientEvent.getTimeStamp(), 0);
                            break;
                        case CLIENT_SEND:
                            assertEquals(getUrlString(), thClientEvent.getEndpoint().getStringValue());
                            break;
                        case CLIENT_RECEIVE:
                            assertEquals(new Integer(200), thClientEvent.getThriftResponseStatus());
                            assertEquals("OK", thClientEvent.getThriftResponseMessage());
                            break;
                        case SERVICE_RESULT:
                            assertEquals(new Owner(1, "name1"), thClientEvent.getCallResult());
                            break;
                        case ERROR:
                        default:
                            fail("Should not be invoked on success");
                    }


                });
        try {
            client.getOwner(1);
        } catch (TException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUnexpectedError() throws TException {
        addServlet(createMutableTServlet(OwnerServiceSrv.Iface.class, handler), "/");

        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    switch (thClientEvent.getEventType()) {
                        case CALL_SERVICE:
                            assertArrayEquals(new Object[] {0}, thClientEvent.getCallArguments());
                            assertEquals("getOwner", thClientEvent.getCallName());
                            assertEquals(CallType.CALL, thClientEvent.getCallType());
                            assertEquals(TraceContext.NO_PARENT_ID, thClientEvent.getParentId());
                            assertNotNull(thClientEvent.getTraceId());
                            assertEquals(thClientEvent.getTraceId(), thClientEvent.getSpanId());
                            assertNull(thClientEvent.getEndpoint());
                            assertNotEquals(thClientEvent.getTimeStamp(), 0);
                            break;
                        case CLIENT_SEND:
                            assertEquals(getUrlString(), thClientEvent.getEndpoint().getStringValue());
                            break;
                        case CLIENT_RECEIVE:
                            assertEquals(new Integer(500), thClientEvent.getThriftResponseStatus());
                            assertEquals("Server Error", thClientEvent.getThriftResponseMessage());
                            break;
                        case SERVICE_RESULT:
                            fail("Should not be invoked on error");
                            break;
                        case ERROR:
                            assertFalse(thClientEvent.isSuccessfulCall());
                            assertEquals(WErrorType.UNEXPECTED_ERROR,
                                    thClientEvent.getErrorDefinition().getErrorType());
                            assertEquals("Error was generated outside of the client", WErrorSource.EXTERNAL,
                                    thClientEvent.getErrorDefinition().getGenerationSource());
                            assertEquals("This is internal service error", WErrorSource.INTERNAL,
                                    thClientEvent.getErrorDefinition().getErrorSource());
                            assertEquals("RuntimeException:err", thClientEvent.getErrorDefinition().getErrorReason());
                            break;
                        default:
                            fail();
                    }


                });
        try {
            client.getOwner(0);
        } catch (WRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUnavailableResultError() throws TException {
        addServlet(createMutableTServlet(OwnerServiceSrv.Iface.class, handler), "/");
        AtomicBoolean hasErr = new AtomicBoolean();
        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                        assertFalse(thClientEvent.isSuccessfulCall());
                        assertEquals(new Integer(503), thClientEvent.getThriftResponseStatus());
                        hasErr.set(true);
                    }
                });
        try {
            client.getOwner(10);
            fail();
        } catch (WUnavailableResultException e) {
            assertTrue(hasErr.get());
            e.printStackTrace();
        }
    }

    @Test
    public void testUndefinedResultError() throws TException {
        addServlet(createMutableTServlet(OwnerServiceSrv.Iface.class, handler), "/");
        AtomicBoolean hasErr = new AtomicBoolean();
        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                        assertFalse(thClientEvent.isSuccessfulCall());
                        assertEquals(new Integer(504), thClientEvent.getThriftResponseStatus());
                        hasErr.set(true);
                    }
                });
        try {
            client.getOwner(20);
            fail();
        } catch (WUndefinedResultException e) {
            assertTrue(hasErr.get());
            e.printStackTrace();
        }
    }

}
