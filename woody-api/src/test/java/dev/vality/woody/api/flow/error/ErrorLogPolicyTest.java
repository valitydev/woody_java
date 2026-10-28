package dev.vality.woody.api.flow.error;

import org.junit.Test;
import org.slf4j.event.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ErrorLogPolicyTest {

    static class NotFound extends Exception {
    }

    static class PartyNotFound extends NotFound {
    }

    static class InvalidRequest extends Exception {
    }

    @Test
    public void defaultPolicyLogsEverythingAsErrorWithStacktrace() {
        ErrorLogRule rule = ErrorLogPolicy.DEFAULT.resolve(new PartyNotFound());
        assertEquals(Level.ERROR, rule.getLevel());
        assertTrue(rule.isStacktrace());
        assertSame(ErrorLogPolicy.DEFAULT, ErrorLogPolicy.builder().build());
    }

    @Test
    public void levelsArePerClassAndMostSpecificWins() {
        ErrorLogPolicy policy = ErrorLogPolicy.builder()
                .level(NotFound.class, Level.WARN)
                .level(PartyNotFound.class, Level.DEBUG)
                .level(InvalidRequest.class, Level.INFO)
                .build();

        assertEquals(Level.DEBUG, policy.resolve(new PartyNotFound()).getLevel());
        assertEquals(Level.WARN, policy.resolve(new NotFound() {
        }).getLevel());
        assertEquals(Level.INFO, policy.resolve(new InvalidRequest()).getLevel());
        assertEquals(Level.ERROR, policy.resolve(new IllegalStateException()).getLevel());
    }

    @Test
    public void stacktraceIsWrittenUnlessDisabled() {
        ErrorLogPolicy policy = ErrorLogPolicy.builder()
                .levelWithoutStacktrace(PartyNotFound.class, Level.WARN)
                .level(InvalidRequest.class, Level.WARN)
                .levelWithoutStacktrace(IllegalArgumentException.class, Level.ERROR)
                .build();

        assertFalse(policy.resolve(new PartyNotFound()).isStacktrace());
        assertTrue(policy.resolve(new InvalidRequest()).isStacktrace());
        assertFalse(policy.resolve(new IllegalArgumentException()).isStacktrace());
        assertTrue(policy.resolve(new IllegalStateException()).isStacktrace());
    }
}
