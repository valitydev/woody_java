package dev.vality.woody.api.event;

public interface ClientEventListener<E extends ClientEvent> extends EventListener<E> {
    void notifyEvent(E event);

}
