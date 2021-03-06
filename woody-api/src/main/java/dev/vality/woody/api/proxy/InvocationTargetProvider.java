package dev.vality.woody.api.proxy;

/**
 * This interface is used for hiding implementation logic of proxied target creation.
 */
public interface InvocationTargetProvider<T> {
    /**
     * @return target type without creating target instance.
     */
    Class<T> getTargetType();

    /**
     * @return target instance.
     */
    T getTarget();

    /**
     * Use this method return used target.
     */
    void releaseTarget(T target);

    /**
     * @return true - if it's guaranteed that returned target instance is always the same. false - if not.
     */
    boolean isSingleTarget();
}
