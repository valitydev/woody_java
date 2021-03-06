package dev.vality.woody.api.proxy.tracer;

import dev.vality.woody.api.proxy.InstanceMethodCaller;

public interface MethodCallTracer {
    void beforeCall(Object[] args, InstanceMethodCaller caller) throws Exception;

    void afterCall(Object[] args, InstanceMethodCaller caller, Object result) throws Exception;

    void callError(Object[] args, InstanceMethodCaller caller, Throwable error) throws Exception;

}
