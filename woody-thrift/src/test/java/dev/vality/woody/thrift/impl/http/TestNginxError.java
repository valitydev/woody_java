package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.event.ClientEventType;
import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorSource;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.thrift.impl.http.event.THClientEvent;
import org.apache.thrift.TException;
import org.junit.Test;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class TestNginxError extends AbstractTest {
    @Test
    public void testNginx500Error() throws TException {
        addServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                throw new RuntimeException("Unexpected nginx error");
            }
        }, "/");
        AtomicBoolean hasErr = new AtomicBoolean();
        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                        assertFalse(thClientEvent.isSuccessfulCall());
                        assertEquals(new Integer(500), thClientEvent.getThriftResponseStatus());
                        WErrorDefinition errorDefinition = thClientEvent.getErrorDefinition();
                        assertEquals(errorDefinition.getErrorSource(), WErrorSource.INTERNAL);
                        assertEquals(errorDefinition.getGenerationSource(), WErrorSource.EXTERNAL);
                        assertEquals(errorDefinition.getErrorType(), WErrorType.UNEXPECTED_ERROR);
                        assertNull(errorDefinition.getErrorReason());
                        assertNull(errorDefinition.getErrorName());
                        hasErr.set(true);
                    }
                });
        try {
            client.getOwner(0);
            fail();
        } catch (RuntimeException e) {
            assertTrue(hasErr.get());
            e.printStackTrace();
        }
    }

    @Test
    public void testNginxOk() throws TException {
        addServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.getWriter().write("OK");
            }
        }, "/");
        AtomicBoolean hasErr = new AtomicBoolean();
        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                        assertFalse(thClientEvent.isSuccessfulCall());
                        assertEquals(new Integer(HttpServletResponse.SC_OK),
                                thClientEvent.getThriftResponseStatus());
                        WErrorDefinition errorDefinition = thClientEvent.getErrorDefinition();
                        assertEquals(WErrorSource.INTERNAL, errorDefinition.getErrorSource());
                        assertEquals(WErrorSource.INTERNAL, errorDefinition.getGenerationSource());
                        assertEquals(WErrorType.PROVIDER_ERROR, errorDefinition.getErrorType());
                        hasErr.set(true);
                    }
                });
        try {
            client.getOwner(0);
            fail();
        } catch (RuntimeException e) {
            assertTrue(hasErr.get());
            e.printStackTrace();
        }
    }

    @Test
    public void testNginx502Error() throws TException {
        addServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.sendError(HttpServletResponse.SC_BAD_GATEWAY);
            }
        }, "/");
        AtomicBoolean hasErr = new AtomicBoolean();
        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                        assertFalse(thClientEvent.isSuccessfulCall());
                        assertEquals(new Integer(HttpServletResponse.SC_BAD_GATEWAY),
                                thClientEvent.getThriftResponseStatus());
                        WErrorDefinition errorDefinition = thClientEvent.getErrorDefinition();
                        assertEquals(WErrorSource.EXTERNAL, errorDefinition.getErrorSource());
                        assertEquals(WErrorSource.EXTERNAL, errorDefinition.getGenerationSource());
                        assertEquals(WErrorType.UNEXPECTED_ERROR, errorDefinition.getErrorType());
                        assertNull(errorDefinition.getErrorReason());
                        assertNull(errorDefinition.getErrorName());
                        hasErr.set(true);
                    }
                });
        try {
            client.getOwner(0);
            fail();
        } catch (RuntimeException e) {
            assertTrue(hasErr.get());
            e.printStackTrace();
        }
    }

    @Test
    public void testNginx4xxError() throws TException {
        addServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.sendError(HttpServletResponse.SC_CONFLICT);
            }
        }, "/");
        AtomicBoolean hasErr = new AtomicBoolean();
        OwnerServiceSrv.Iface client = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                (ClientEventListener<THClientEvent>) (THClientEvent thClientEvent) -> {
                    if (thClientEvent.getEventType() == ClientEventType.ERROR) {
                        assertFalse(thClientEvent.isSuccessfulCall());
                        assertEquals(new Integer(HttpServletResponse.SC_CONFLICT),
                                thClientEvent.getThriftResponseStatus());
                        WErrorDefinition errorDefinition = thClientEvent.getErrorDefinition();
                        assertEquals(WErrorSource.INTERNAL, errorDefinition.getErrorSource());
                        assertEquals(WErrorSource.EXTERNAL, errorDefinition.getGenerationSource());
                        assertEquals(WErrorType.UNEXPECTED_ERROR, errorDefinition.getErrorType());
                        assertNull(errorDefinition.getErrorReason());
                        assertNull(errorDefinition.getErrorName());
                        hasErr.set(true);
                    }
                });
        try {
            client.getOwner(0);
            fail();
        } catch (RuntimeException e) {
            assertTrue(hasErr.get());
            e.printStackTrace();
        }
    }
}
