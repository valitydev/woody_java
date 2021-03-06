package dev.vality.woody.api.flow.error;

import java.util.Objects;

public class WRuntimeException extends RuntimeException {
    private final WErrorDefinition errorDefinition;

    public WRuntimeException(WErrorDefinition errorDefinition) {
        Objects.requireNonNull(errorDefinition);
        this.errorDefinition = errorDefinition;
    }

    public WRuntimeException(String message, WErrorDefinition errorDefinition) {
        super(message);
        Objects.requireNonNull(errorDefinition);
        this.errorDefinition = errorDefinition;
    }

    public WRuntimeException(String message, Throwable cause, WErrorDefinition errorDefinition) {
        super(message, cause);
        Objects.requireNonNull(errorDefinition);
        this.errorDefinition = errorDefinition;
    }

    public WRuntimeException(Throwable cause, WErrorDefinition errorDefinition) {
        super(cause);
        Objects.requireNonNull(errorDefinition);
        this.errorDefinition = errorDefinition;
    }

    public WRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                             WErrorDefinition errorDefinition) {
        super(message, cause, enableSuppression, writableStackTrace);
        Objects.requireNonNull(errorDefinition);
        this.errorDefinition = errorDefinition;
    }

    public WErrorDefinition getErrorDefinition() {
        return errorDefinition;
    }

    @Override
    public String toString() {
        String msg = super.toString();
        return "WRuntimeException{" + "errorDefinition=" + errorDefinition + ", " + msg + "}";
    }
}
