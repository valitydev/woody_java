package dev.vality.woody.api.interceptor.ext;

import dev.vality.woody.api.interceptor.Interceptor;
import dev.vality.woody.api.trace.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtendableInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendableInterceptor.class);

    private final InterceptorExtension[] extensions;
    private final BiFunction<TraceData, Throwable, Boolean> errContextResolver;

    public ExtendableInterceptor(List<InterceptorExtension> extensions,
                                 BiFunction<TraceData, Throwable, Boolean> errContextResolver) {
        this.extensions = extensions.toArray(new InterceptorExtension[0]);
        this.errContextResolver = errContextResolver;
    }

    public ExtendableInterceptor(List<ExtensionBundle> extensionBundles,
                                 BiFunction<TraceData, Throwable, Boolean> errContextResolver, boolean isClient,
                                 boolean isRequest) {
        this(extensionBundles.stream().map(bundle -> {
            ExtensionBundle.ContextBundle ctxBundle = isClient ? bundle.getClientBundle() : bundle.getServiceBundle();
            return isRequest ? ctxBundle.getRequestExtension() : ctxBundle.getResponseExtension();
        }).collect(Collectors.toList()), errContextResolver);
    }

    public ExtendableInterceptor(Function<Boolean, List<ExtensionBundle>> primaryBundlesProvider,
                                 List<ExtensionBundle> secondaryBundles, boolean isClient, boolean isRequest) {
        this(concatBundleLists(primaryBundlesProvider.apply(isClient), secondaryBundles),
                (traceData, throwable) -> isClient, isClient, isRequest);
    }

    public static List<ExtensionBundle> concatBundleLists(List<ExtensionBundle> first, List<ExtensionBundle> second) {
        return Stream.concat(first.stream(), second.stream()).collect(Collectors.toList());
    }

    @Override
    public boolean intercept(TraceData traceData, Object providerContext, Object... contextParams) {
        try {
            LOG.trace("Intercept for multiple extensions");
            ExtensionContext extContext = createContext(traceData, providerContext, contextParams);
            initInterception(extContext);
            for (InterceptorExtension extension : extensions) {
                extension.apply(extContext);
            }
            finalizeInterception(extContext);
            return true;
        } catch (Exception e) {
            LOG.trace("Intercept extension error");
            return interceptError(traceData, e, errContextResolver.apply(traceData, e));
        }
    }

    protected ExtensionContext createContext(TraceData traceData, Object providerContext, Object[] contextParams) {
        return new ExtensionContext(traceData, providerContext, contextParams);
    }

    protected void initInterception(ExtensionContext extContext) {
    }

    protected void finalizeInterception(ExtensionContext extContext) {
    }
}
