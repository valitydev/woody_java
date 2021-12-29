package dev.vality.woody.api.interceptor.ext;

@FunctionalInterface
public interface InterceptorExtension<T extends ExtensionContext> {
    /**
     * @throws RuntimeException if any error occurs
     */
    void apply(T extContext);
}
