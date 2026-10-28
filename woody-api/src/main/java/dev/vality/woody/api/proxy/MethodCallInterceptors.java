package dev.vality.woody.api.proxy;

import dev.vality.woody.api.flow.error.ErrorLogPolicy;
import dev.vality.woody.api.flow.error.ErrorLogRule;
import dev.vality.woody.api.proxy.tracer.MethodCallTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class MethodCallInterceptors {

    private static final Logger log = LoggerFactory.getLogger(MethodCallInterceptors.class);

    public static MethodCallInterceptor directCallInterceptor() {
        return (src, args, caller) -> caller.call(src, args);
    }

    public static MethodCallInterceptor trackedCallInterceptor(MethodCallTracer callTracer) {
        return trackedCallInterceptor(callTracer, ErrorLogPolicy.DEFAULT);
    }

    public static MethodCallInterceptor trackedCallInterceptor(MethodCallTracer callTracer,
                                                               ErrorLogPolicy errorLogPolicy) {
        ErrorLogPolicy policy = errorLogPolicy == null ? ErrorLogPolicy.DEFAULT : errorLogPolicy;
        return (src, args, caller) -> {
            Object result;
            callTracer.beforeCall(args, caller);
            try {
                result = caller.call(src, args);
            } catch (Throwable t) {
                logCallError(policy, t);
                callTracer.callError(args, caller, t);
                throw t;
            }
            callTracer.afterCall(args, caller, result);
            return result;
        };
    }

    private static void logCallError(ErrorLogPolicy policy, Throwable t) {
        ErrorLogRule rule = policy.resolve(t);
        if (rule.isStacktrace()) {
            log(rule.getLevel(), "Call Error", t);
        } else {
            log(rule.getLevel(), "Call Error: " + t, null);
        }
    }

    private static void log(Level level, String message, Throwable t) {
        switch (level) {
            case WARN:
                log.warn(message, t);
                break;
            case INFO:
                log.info(message, t);
                break;
            case DEBUG:
                log.debug(message, t);
                break;
            case TRACE:
                log.trace(message, t);
                break;
            default:
                log.error(message, t);
                break;
        }
    }

}
