package dev.vality.woody.api.trace;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

public class TraceData {
    public static final String OTEL_SERVER = "server";
    public static final String OTEL_CLIENT = "client";
    public static final String WOODY = "woody";

    private final ClientSpan clientSpan;
    private final ServiceSpan serviceSpan;

    private Span otelSpan;
    private Context otelContext;
    private Scope activeScope;
    private boolean ownsOtelSpan;
    private boolean preserveOtelSpan;
    private Context pendingParentContext;
    private String inboundTraceParent;
    private String inboundTraceState;

    public TraceData() {
        this.clientSpan = new ClientSpan();
        this.serviceSpan = new ServiceSpan();
        setPendingParentContext(Context.root());
        startNewOtelSpan(OTEL_CLIENT, SpanKind.CLIENT, Context.root());
        openOtelScope();
        this.ownsOtelSpan = false;
        this.preserveOtelSpan = false;
    }

    public TraceData(TraceData oldTraceData) {
        this(oldTraceData, false);
    }

    public TraceData(TraceData oldTraceData, boolean copyCustomServiceMetadata) {
        this.clientSpan = copyCustomServiceMetadata
                ? new ClientSpan(oldTraceData.clientSpan, oldTraceData.serviceSpan.getCustomMetadata())
                : oldTraceData.clientSpan.cloneObject();
        this.serviceSpan = oldTraceData.serviceSpan.cloneObject();
        adoptOtelContext(oldTraceData.getOtelContext());
        this.otelSpan = oldTraceData.otelSpan;
        this.ownsOtelSpan = false;
        this.activeScope = null;
        this.pendingParentContext = oldTraceData.pendingParentContext;
        this.inboundTraceParent = oldTraceData.inboundTraceParent;
        this.inboundTraceState = oldTraceData.inboundTraceState;
        this.preserveOtelSpan = true;
    }

    public TraceData(TraceData oldTraceData, boolean copyCustomServiceMetadata, String resource) {
        this(oldTraceData, copyCustomServiceMetadata);
    }

    public ClientSpan getClientSpan() {
        return clientSpan;
    }

    public ServiceSpan getServiceSpan() {
        return serviceSpan;
    }

    public Span getOtelSpan() {
        return otelSpan;
    }

    public Context getOtelContext() {
        return otelContext;
    }

    public void setPendingParentContext(Context context) {
        this.pendingParentContext = context == null ? Context.root() : context;
    }

    public Context consumePendingParentContext() {
        Context context = pendingParentContext;
        pendingParentContext = Context.root();
        return context;
    }

    public void setInboundTraceParent(String traceParent) {
        this.inboundTraceParent = traceParent;
    }

    public String getInboundTraceParent() {
        return inboundTraceParent;
    }

    public void setInboundTraceState(String traceState) {
        this.inboundTraceState = traceState;
    }

    public String getInboundTraceState() {
        return inboundTraceState;
    }

    public boolean hasValidOtelSpan() {
        return ownsOtelSpan && otelSpan != null && otelSpan.getSpanContext().isValid();
    }

    public void adoptOtelSpan(Span span, Context context, boolean ownsSpan) {
        closeActiveScope();
        if (ownsOtelSpan && otelSpan != null) {
            otelSpan.end();
        }
        if (span == null) {
            this.otelSpan = Span.getInvalid();
            this.otelContext = Context.root();
            this.ownsOtelSpan = false;
            return;
        }
        this.otelSpan = span;
        this.otelContext = context != null ? context : Context.root().with(span);
        this.ownsOtelSpan = ownsSpan && span.getSpanContext().isValid();
        this.preserveOtelSpan = !this.ownsOtelSpan;
    }

    public Span startNewOtelSpan(String spanName, SpanKind spanKind, Context parentContext) {
        closeActiveScope();
        if (otelSpan != null && otelSpan.getSpanContext().isValid()) {
            otelSpan.end();
        }
        Context context = parentContext != null ? parentContext : Context.root();
        Span span = GlobalOpenTelemetry.getTracer(WOODY)
                .spanBuilder(spanName)
                .setSpanKind(spanKind)
                .setParent(context)
                .startSpan();
        this.otelSpan = span;
        this.otelContext = context.with(span);
        this.ownsOtelSpan = true;
        this.preserveOtelSpan = false;
        return span;
    }

    public void setOtelSpan(Span span) {
        adoptOtelSpan(span, span != null ? Context.root().with(span) : Context.root(), true);
    }

    public Scope openOtelScope() {
        closeActiveScope();
        this.activeScope = otelContext.makeCurrent();
        return activeScope;
    }

    public Scope attachOtelContext() {
        return otelContext.makeCurrent();
    }

    public void finishOtelSpan() {
        closeActiveScope();
        if (ownsOtelSpan && otelSpan != null) {
            otelSpan.end();
        }
        otelSpan = Span.getInvalid();
        otelContext = Context.root();
        ownsOtelSpan = false;
        preserveOtelSpan = false;
        inboundTraceParent = null;
        inboundTraceState = null;
    }

    private void closeActiveScope() {
        if (activeScope != null) {
            activeScope.close();
            activeScope = null;
        }
    }

    private void adoptOtelContext(Context context) {
        this.otelContext = context == null ? Context.root() : context;
    }

    /**
     * Checks if {@link ServiceSpan} is filled to determine root:
     * - request initialized by server: span must be filled by server with data referred from client:
     * has filled server span, it's not root by default -> false
     * - request initialized by client, produced by any server request handling event:
     * has filled server span, it's not root -> false
     * - request initialized by client, not produced by any server request handling event:
     * server span not filled, it's root -> true
     *
     * @return true - if root call is running; false - otherwise
     */
    public boolean isRoot() {
        return !serviceSpan.isFilled();
    }

    /**
     * Checks combination of client and server spans to determine current state:
     * Consider this states scheme (S - server span, C - client span; 1 - if it's set,
     * 0 - if not set, determined by checking traceId existence in corresponding span):
     * <p>
     * S | C
     * -----
     * 0 | 0
     * 0 | 1
     * 1 | 0
     * 1 | 1
     * <p>
     * 0,0 and 0,1 combinations don't have server span and context in the state can't be server by default
     * (no server span is set) - it's client state -> true
     * 1,0 means that server span is created and no client span exists - it's server state -> false
     * 1,1 means that both spans exist and child client call is active now because for any client request client span
     * is cleared after call completion, so after child call state returns to (1,0)
     * case - (1,1) is child client state -> true
     * <p>
     * This allows to eliminate the necessity for call processing code to be explicitly configured with expected
     * call state. This can be figured out directly from the context in runtime.
     * The only exclusion is {@link dev.vality.woody.api.trace.context.TraceContext} itself. It uses already filled
     * trace id field for server state initialization
     *
     * @return true - if call is running as root client or child client call for server request handling;
     *         false - if call is running in server request handing
     */
    public boolean isClient() {
        return !serviceSpan.isFilled() || clientSpan.isFilled();
    }

    public ContextSpan getActiveSpan() {
        return isClient() ? clientSpan : serviceSpan;
    }

    public ContextSpan getSpan(boolean isClient) {
        return isClient ? clientSpan : serviceSpan;
    }

    public void reset() {
        clientSpan.reset();
        serviceSpan.reset();
        finishOtelSpan();
        setPendingParentContext(Context.root());
        inboundTraceParent = null;
        inboundTraceState = null;
    }

    public TraceData cloneObject() {
        return new TraceData(this);
    }

    public boolean shouldPreserveOtelSpan() {
        return preserveOtelSpan && otelSpan != null && otelSpan.getSpanContext().isValid();
    }

    public void clearPreserveOtelSpan() {
        this.preserveOtelSpan = false;
    }
}
