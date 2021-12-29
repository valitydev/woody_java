package dev.vality.woody.api.event;

public interface EventListener<E extends Event> {
    void notifyEvent(E event);
}
