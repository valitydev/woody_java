package dev.vality.woody.api.trace.context.metadata;

public interface MetadataExtensionKit<T> {
    MetadataExtension<T> getExtension();

    MetadataConverter<T> getConverter();
}
