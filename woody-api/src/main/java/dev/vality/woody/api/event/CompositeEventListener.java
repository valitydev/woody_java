package dev.vality.woody.api.event;

public class CompositeEventListener<E extends Event> implements EventListener<E> {
    private final EventListener<E>[] listeners;

    public CompositeEventListener(EventListener<E>... listeners) {
        this.listeners = listeners.clone();
    }

    @Override
    public void notifyEvent(E event) {
        for (int i = 0; i < listeners.length; ++i) {
            listeners[i].notifyEvent(event);
        }
    }
}
