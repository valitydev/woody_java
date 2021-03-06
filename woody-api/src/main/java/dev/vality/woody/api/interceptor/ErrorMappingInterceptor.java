package dev.vality.woody.api.interceptor;

import dev.vality.woody.api.flow.error.ErrorMapProcessor;
import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.BiConsumer;

public class ErrorMappingInterceptor extends EmptyCommonInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorMappingInterceptor.class);

    private final ErrorMapProcessor errorProcessor;
    private final BiConsumer<WErrorDefinition, ContextSpan> errDefConsumer;

    public ErrorMappingInterceptor(ErrorMapProcessor errorProcessor,
                                   BiConsumer<WErrorDefinition, ContextSpan> errDefConsumer) {
        Objects.requireNonNull(errorProcessor);
        Objects.requireNonNull(errDefConsumer);
        this.errorProcessor = errorProcessor;
        this.errDefConsumer = errDefConsumer;
    }

    @Override
    public boolean interceptResponse(TraceData traceData, Object providerContext, Object... contextParams) {
        LOG.trace("Intercept response. Trying to map error");
        WErrorDefinition errorDefinition = errorProcessor.processMapToDef(TraceContext.getCurrentTraceData());
        if (errorDefinition != null) {
            ContextSpan contextSpan = TraceContext.getCurrentTraceData().getActiveSpan();
            contextSpan.getMetadata().putValue(MetadataProperties.ERROR_DEFINITION, errorDefinition);
            errDefConsumer.accept(errorDefinition, contextSpan);
        }
        return true;
    }
}
