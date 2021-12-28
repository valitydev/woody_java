package dev.vality.woody.api.proxy;

import dev.vality.woody.api.proxy.tracer.EventTracer;
import dev.vality.woody.api.proxy.tracer.MethodCallTracer;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertSame;

@Ignore
public class TestProxyInvocationFactory {
    @Test
    public void testString() {
        Srv directImpl = () -> "string";

        MethodCallTracer wrappedCallTracer = new EventTracer();

        ProxyFactory reflectionProxyFactory = new ProxyFactory(new ReflectionMethodCallerFactory(), wrappedCallTracer, false);
        ProxyFactory handleProxyFactory = new ProxyFactory(new HandleMethodCallerFactory(), wrappedCallTracer, false);

        Srv directLambda = () -> "string";
        Srv refDirectProxy = reflectionProxyFactory.getInstance(Srv.class, new SingleTargetProvider<>(directImpl));
        Srv refLambdaProxy = reflectionProxyFactory.getInstance(Srv.class, new SingleTargetProvider<>(directLambda));
        Srv handleDirectProxy = handleProxyFactory.getInstance(Srv.class, new SingleTargetProvider<>(directImpl));
        Srv handleLambdaProxy = handleProxyFactory.getInstance(Srv.class, new SingleTargetProvider<>(directLambda));
        handleDirectProxy.getString();
        handleLambdaProxy.getString();
        for (int i = 0; i < 1000000; i++) {
            assertSame(directImpl.getString(), refDirectProxy.getString());
            assertSame(directImpl.getString(), refLambdaProxy.getString());
            assertSame(directImpl.getString(), handleDirectProxy.getString());
            assertSame(directImpl.getString(), handleLambdaProxy.getString());
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            directImpl.getString();
        }
        System.out.println("Direct:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            directLambda.getString();
        }
        System.out.println("Lambda:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            refDirectProxy.getString();
        }
        System.out.println("Refl Direct roxy:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            refLambdaProxy.getString();
        }
        System.out.println("Refl Lambda roxy:" + (System.currentTimeMillis() - start));


        start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            handleDirectProxy.getString();
        }
        System.out.println("Handle Direct Proxy:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            handleLambdaProxy.getString();
        }
        System.out.println("Handle Lambda Proxy:" + (System.currentTimeMillis() - start));

    }

    private interface Srv {
        String getString();
    }
}
