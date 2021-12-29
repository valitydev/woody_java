package dev.vality.woody.api.trace;

public interface Endpoint<T> {
    String getStringValue();

    T getValue();
}
