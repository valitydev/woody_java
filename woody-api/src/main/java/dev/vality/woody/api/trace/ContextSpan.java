package dev.vality.woody.api.trace;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.OpenTelemetry;

public class ContextSpan {

    private static final OpenTelemetry openTelemetry = ExampleConfiguration.initOpenTelemetry();

    protected final Span span;
    protected final Metadata metadata;
    protected final Metadata customMetadata;
    protected final io.opentelemetry.api.trace.Span otelSpan;
    private static final Tracer tracer =
            openTelemetry.getTracer("io.opentelemetry.example.http.HttpClient");

    public ContextSpan() {
        span = new Span();
        otelSpan = tracer.spanBuilder("/").setSpanKind(SpanKind.CLIENT).startSpan();
        metadata = new Metadata();
        customMetadata = new Metadata(false);
    }

    protected ContextSpan(ContextSpan oldSpan) {
        this.span = oldSpan.span.cloneObject();
        this.metadata = oldSpan.metadata.cloneObject();
        this.otelSpan = oldSpan.otelSpan;
        this.customMetadata = oldSpan.customMetadata.cloneObject();
    }

    protected ContextSpan(ContextSpan oldSpan, Metadata customMetadata) {
        this.span = oldSpan.span.cloneObject();
        this.metadata = oldSpan.metadata.cloneObject();
        this.otelSpan = oldSpan.otelSpan;
        this.customMetadata = customMetadata.cloneObject();
    }

    public Span getSpan() {
        return span;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Metadata getCustomMetadata() {
        return customMetadata;
    }

    public io.opentelemetry.api.trace.Span getOtelSpan() {
        return otelSpan;
    }

    public boolean isFilled() {
        return span.isFilled();
    }

    public boolean isStarted() {
        return span.isStarted();
    }

    public void reset() {
        span.reset();
        otelSpan.end();
        metadata.reset();
        customMetadata.reset();
    }

    public ContextSpan cloneObject() {
        return new ContextSpan(this);
    }
}
