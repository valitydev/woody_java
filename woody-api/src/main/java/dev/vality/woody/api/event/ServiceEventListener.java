package dev.vality.woody.api.event;

public interface ServiceEventListener<E extends ServiceEvent> extends EventListener<E> {
    void notifyEvent(E event);
}
