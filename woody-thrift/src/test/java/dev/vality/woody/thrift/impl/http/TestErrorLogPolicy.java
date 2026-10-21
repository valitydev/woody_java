package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.flow.error.ErrorLogPolicy;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestErrorLogPolicy extends AbstractTest {

    private static final String INTERCEPTOR_LOGGER = "dev.vality.woody.api.proxy.MethodCallInterceptors";

    private final List<LoggingEvent> events = new CopyOnWriteArrayList<>();
    private final AppenderSkeleton appender = new AppenderSkeleton() {
        @Override
        protected void append(LoggingEvent event) {
            events.add(event);
        }

        @Override
        public void close() {
            // Intentionally left empty: in-memory appender holds no resources
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    };

    private final OwnerServiceSrv.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getOwner(int id) {
            throw new IllegalStateException("system failure");
        }
    };

    @Before
    public void addAppender() {
        Logger.getRootLogger().addAppender(appender);
    }

    @After
    public void removeAppender() {
        Logger.getRootLogger().removeAppender(appender);
    }

    @Test
    public void errorsAreLoggedAsErrorByDefault() throws Exception {
        OwnerServiceSrv.Iface client = setUp(null, null);

        callGetErrOwner(client);

        List<LoggingEvent> interceptorEvents = interceptorEvents();
        assertEquals("client and server records expected: " + describe(), 2, interceptorEvents.size());
        interceptorEvents.forEach(e -> {
            assertEquals(Level.ERROR, e.getLevel());
            assertTrue(e.getThrowableInformation().getThrowable() instanceof test_error);
        });
    }

    @Test
    public void configuredErrorIsLoggedWithConfiguredLevelWithoutStacktrace() throws Exception {
        OwnerServiceSrv.Iface client = setUp(
                ErrorLogPolicy.builder().levelWithoutStacktrace(test_error.class, org.slf4j.event.Level.WARN).build(),
                ErrorLogPolicy.builder().levelWithoutStacktrace(test_error.class, org.slf4j.event.Level.INFO).build());

        callGetErrOwner(client);

        List<LoggingEvent> interceptorEvents = interceptorEvents();
        assertEquals("client and server records expected: " + describe(), 2, interceptorEvents.size());
        assertEquals(Set.of(Level.WARN, Level.INFO),
                interceptorEvents.stream().map(LoggingEvent::getLevel).collect(Collectors.toSet()));
        interceptorEvents.forEach(e -> assertTrue(e.getRenderedMessage().contains("test_error(id:42)")));
        assertFalse("listed error must not be logged as ERROR: " + describe(),
                events.stream().anyMatch(e -> e.getLevel().isGreaterOrEqual(Level.ERROR)));
        assertTrue("listed error must be logged without stacktrace: " + describe(),
                interceptorEvents.stream().allMatch(e -> e.getThrowableInformation() == null));
    }

    @Test
    public void configuredErrorIsLoggedWithStacktraceByDefault() throws Exception {
        OwnerServiceSrv.Iface client = setUp(
                ErrorLogPolicy.builder().level(test_error.class, org.slf4j.event.Level.WARN).build(), null);

        callGetErrOwner(client);

        assertTrue("server record must be WARN with stacktrace: " + describe(),
                interceptorEvents().stream().anyMatch(e -> e.getLevel() == Level.WARN
                        && e.getThrowableInformation() != null
                        && e.getThrowableInformation().getThrowable() instanceof test_error));
    }

    @Test
    public void notListedErrorIsStillLoggedAsError() throws Exception {
        OwnerServiceSrv.Iface client = setUp(
                ErrorLogPolicy.builder().level(test_error.class, org.slf4j.event.Level.WARN).build(), null);

        try {
            client.getOwner(1);
            fail("error expected");
        } catch (RuntimeException expected) {
            // ok
        }

        assertTrue("system error must be logged as ERROR with stacktrace: " + describe(),
                interceptorEvents().stream().anyMatch(e -> e.getLevel() == Level.ERROR
                        && e.getThrowableInformation() != null
                        && e.getThrowableInformation().getThrowable() instanceof IllegalStateException));
        assertTrue(interceptorEvents().stream().noneMatch(e -> e.getLevel() == Level.WARN));
    }

    private OwnerServiceSrv.Iface setUp(ErrorLogPolicy serverPolicy, ErrorLogPolicy clientPolicy)
            throws URISyntaxException {
        addServlet(new THServiceBuilder()
                .withErrorLogPolicy(serverPolicy)
                .build(OwnerServiceSrv.Iface.class, handler), "/");
        return new THClientBuilder()
                .withAddress(new URI(getUrlString()))
                .withErrorLogPolicy(clientPolicy)
                .build(OwnerServiceSrv.Iface.class);
    }

    private void callGetErrOwner(OwnerServiceSrv.Iface client) throws TException {
        try {
            client.getErrOwner(42);
            fail("test_error expected");
        } catch (test_error e) {
            assertEquals(42, e.getId());
        }
    }

    private List<LoggingEvent> interceptorEvents() {
        return events.stream()
                .filter(e -> INTERCEPTOR_LOGGER.equals(e.getLoggerName()))
                .collect(Collectors.toList());
    }

    private String describe() {
        return events.stream()
                .map(e -> String.join(" ", String.valueOf(e.getLevel()), e.getLoggerName(),
                        e.getRenderedMessage(), e.getThrowableInformation() != null ? "[throwable]" : ""))
                .collect(Collectors.joining("\n", "\n", ""));
    }
}
