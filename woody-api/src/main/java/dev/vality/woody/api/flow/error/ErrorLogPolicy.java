package dev.vality.woody.api.flow.error;

import org.slf4j.event.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Defines how woody logs errors thrown from a call: level and whether stacktrace is written.
 * <p>
 * By default every error is logged as {@link Level#ERROR}; stacktrace is always written unless disabled explicitly.
 * Rules can be configured per error class:
 * <pre>{@code
 * new THServiceBuilder()
 *         .withErrorLogPolicy(ErrorLogPolicy.builder()
 *                 .level(InvalidRequest.class, Level.WARN)
 *                 .levelWithoutStacktrace(PartyNotFound.class, Level.WARN)
 *                 .build())
 *         .build(Iface.class, handler);
 * }</pre>
 * A rule configured for a class is applied to its subclasses too; the most specific class wins.
 */
public final class ErrorLogPolicy {

    public static final ErrorLogPolicy DEFAULT = new ErrorLogPolicy(Map.of());

    private static final ErrorLogRule DEFAULT_RULE = new ErrorLogRule(Level.ERROR, true);

    private final Map<Class<? extends Throwable>, ErrorLogRule> rules;

    private ErrorLogPolicy(Map<Class<? extends Throwable>, ErrorLogRule> rules) {
        this.rules = Map.copyOf(rules);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return logging rule for the error, never {@code null}
     */
    public ErrorLogRule resolve(Throwable error) {
        for (Class<?> type = error.getClass(); type != null; type = type.getSuperclass()) {
            ErrorLogRule rule = rules.get(type);
            if (rule != null) {
                return rule;
            }
        }
        return DEFAULT_RULE;
    }

    public static final class Builder {
        private final Map<Class<? extends Throwable>, ErrorLogRule> rules = new HashMap<>();

        private Builder() {
        }

        /**
         * Log errors of this class (and its subclasses) with given level and stacktrace.
         */
        public Builder level(Class<? extends Throwable> errorType, Level level) {
            return rule(errorType, level, true);
        }

        /**
         * Log errors of this class (and its subclasses) with given level as a single line without stacktrace.
         */
        public Builder levelWithoutStacktrace(Class<? extends Throwable> errorType, Level level) {
            return rule(errorType, level, false);
        }

        private Builder rule(Class<? extends Throwable> errorType, Level level, boolean stacktrace) {
            rules.put(Objects.requireNonNull(errorType, "errorType"),
                    new ErrorLogRule(Objects.requireNonNull(level, "level"), stacktrace));
            return this;
        }

        public ErrorLogPolicy build() {
            return rules.isEmpty() ? DEFAULT : new ErrorLogPolicy(rules);
        }
    }
}
