package dev.vality.woody.api.flow.error;

import org.slf4j.event.Level;

/**
 * How a single error is logged: level and whether stacktrace is written. Resolved by {@link ErrorLogPolicy}.
 */
public final class ErrorLogRule {

    private final Level level;
    private final boolean stacktrace;

    ErrorLogRule(Level level, boolean stacktrace) {
        this.level = level;
        this.stacktrace = stacktrace;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isStacktrace() {
        return stacktrace;
    }
}
