package dev.vality.woody.thrift.impl.http.interceptor.ext;

import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.interceptor.ext.ExtensionBundle;
import dev.vality.woody.api.interceptor.ext.InterceptorExtension;
import dev.vality.woody.api.trace.*;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;
import dev.vality.woody.thrift.impl.http.THResponseInfo;
import dev.vality.woody.thrift.impl.http.error.THProviderErrorMapper;
import dev.vality.woody.thrift.impl.http.interceptor.THRequestInterceptionException;
import dev.vality.woody.thrift.impl.http.transport.THttpHeader;
import dev.vality.woody.thrift.impl.http.transport.TTransportErrorType;
import dev.vality.woody.thrift.impl.http.transport.UrlStringEndpoint;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.semconv.HttpAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.vality.woody.api.interceptor.ext.ExtensionBundle.ContextBundle.createCtxBundle;
import static dev.vality.woody.api.interceptor.ext.ExtensionBundle.createExtBundle;
import static dev.vality.woody.api.interceptor.ext.ExtensionBundle.createServiceExtBundle;
import static java.util.AbstractMap.SimpleEntry;

public class TransportExtensionBundles {
    private static final Logger log = LoggerFactory.getLogger(TransportExtensionBundles.class);

    public static final ExtensionBundle TRANSPORT_CONFIG_BUNDLE =
            createServiceExtBundle(createCtxBundle((InterceptorExtension<THSExtensionContext>) reqSCtx -> {
                String reqMethod = reqSCtx.getProviderRequest().getMethod();
                if (!"POST".equals(reqMethod)) {
                    throw new THRequestInterceptionException(TTransportErrorType.BAD_REQUEST_TYPE, reqMethod);
                }
                String cType = reqSCtx.getProviderRequest().getContentType();
                if (!"application/x-thrift".equalsIgnoreCase(cType)) {
                    throw new THRequestInterceptionException(TTransportErrorType.BAD_CONTENT_TYPE, cType);
                }
            }, respSCtx -> {
            }));

    public static final ExtensionBundle DEADLINE_BUNDLE =
            createExtBundle(createCtxBundle((InterceptorExtension<THCExtensionContext>) reqCCtx -> {
                ClientSpan clientSpan = reqCCtx.getTraceData().getClientSpan();
                Instant deadline = ContextUtils.getDeadline(clientSpan);
                if (deadline != null) {
                    reqCCtx.setRequestHeader(THttpHeader.DEADLINE.getKey(), deadline.toString());
                }
            }, respCCtx -> {
            }), createCtxBundle((InterceptorExtension<THSExtensionContext>) reqSCtx -> {
                HttpServletRequest request = reqSCtx.getProviderRequest();
                ServiceSpan serviceSpan = reqSCtx.getTraceData().getServiceSpan();
                String deadlineHeader = THttpHeader.DEADLINE.getKey();
                String deadlineHeaderValue = request.getHeader(deadlineHeader);
                if (deadlineHeaderValue != null) {
                    try {
                        Instant deadline = Instant.parse(deadlineHeaderValue);
                        ContextUtils.setDeadline(serviceSpan, deadline);
                    } catch (DateTimeParseException ex) {
                        throw new THRequestInterceptionException(TTransportErrorType.BAD_HEADER, deadlineHeader, ex);
                    }
                }
            }, (InterceptorExtension<THSExtensionContext>) respSCtx -> {
                Instant deadline = ContextUtils.getDeadline(respSCtx.getTraceData().getServiceSpan());
                if (deadline != null) {
                    respSCtx.setResponseHeader(THttpHeader.DEADLINE.getKey(), deadline.toString());
                }
            }));

    public static final ExtensionBundle CALL_ENDPOINT_BUNDLE =
            createExtBundle(createCtxBundle((InterceptorExtension<THCExtensionContext>) reqCCtx -> {
                ContextSpan contextSpan = reqCCtx.getTraceData().getClientSpan();
                URL url = reqCCtx.getRequestCallEndpoint();
                contextSpan.getMetadata().putValue(MetadataProperties.CALL_ENDPOINT,
                        new UrlStringEndpoint(url == null ? null : url.toString()));
            }, respCCtx -> {
            }), createCtxBundle((InterceptorExtension<THSExtensionContext>) reqSCtx -> {
                HttpServletRequest request = reqSCtx.getProviderRequest();
                String queryString = request.getQueryString();
                StringBuffer sb = request.getRequestURL();
                if (queryString != null) {
                    sb.append('?').append(request.getQueryString());
                }
                reqSCtx.getTraceData().getServiceSpan().getMetadata()
                        .putValue(MetadataProperties.CALL_ENDPOINT, new UrlStringEndpoint(sb.toString()));
            }, reqSCtx -> {
            }));

    public static final ExtensionBundle TRANSPORT_INJECTION_BUNDLE =
            createExtBundle(createCtxBundle((InterceptorExtension<THCExtensionContext>) reqCCtx -> {
                reqCCtx.getTraceData().getClientSpan().getMetadata()
                        .putValue(THMetadataProperties.TH_TRANSPORT_REQUEST, reqCCtx.getProviderContext());
            }, (InterceptorExtension<THCExtensionContext>) respCCtx -> {
                respCCtx.getTraceData().getClientSpan().getMetadata()
                        .putValue(THMetadataProperties.TH_TRANSPORT_RESPONSE, respCCtx.getProviderContext());
            }), createCtxBundle((InterceptorExtension<THSExtensionContext>) reqSCtx -> {
                HttpServletResponse response =
                        ContextUtils.getContextValue(HttpServletResponse.class, reqSCtx.getContextParameters(), 0);
                ServiceSpan serviceSpan = reqSCtx.getTraceData().getServiceSpan();
                serviceSpan.getMetadata()
                        .putValue(THMetadataProperties.TH_TRANSPORT_REQUEST, reqSCtx.getProviderRequest());
                serviceSpan.getMetadata().putValue(THMetadataProperties.TH_TRANSPORT_RESPONSE, response);
            }, respSCtx -> {
            }));

    public static final ExtensionBundle RPC_ID_BUNDLE =
            createExtBundle(createCtxBundle((InterceptorExtension<THCExtensionContext>) reqCCtx -> {
                Span span = reqCCtx.getTraceData().getClientSpan().getSpan();
                reqCCtx.setRequestHeader(THttpHeader.TRACE_ID.getKey(), span.getTraceId());
                reqCCtx.setRequestHeader(THttpHeader.SPAN_ID.getKey(), span.getId());
                reqCCtx.setRequestHeader(THttpHeader.PARENT_ID.getKey(), span.getParentId());
                injectTraceHeaders(reqCCtx);
            }, (InterceptorExtension<THCExtensionContext>) respCCtx -> {
                applyResponseStatus(respCCtx.getTraceData(), respCCtx.getResponseStatus());
            }), createCtxBundle((InterceptorExtension<THSExtensionContext>) reqSCtx -> {
                extractTraceContext(reqSCtx);
                HttpServletRequest request = reqSCtx.getProviderRequest();
                Span span = reqSCtx.getTraceData().getServiceSpan().getSpan();
                List<Map.Entry<THttpHeader, Consumer<String>>> headerConsumers =
                        Arrays.asList(
                                new SimpleEntry<>(THttpHeader.TRACE_ID, span::setTraceId),
                                new SimpleEntry<>(THttpHeader.PARENT_ID, span::setParentId),
                                new SimpleEntry<>(THttpHeader.SPAN_ID, span::setId)
                        );
                validateAndProcessTraceHeaders(request, THttpHeader::getKey, headerConsumers);
            }, (InterceptorExtension<THSExtensionContext>) respSCtx -> {
                Span span = respSCtx.getTraceData().getServiceSpan().getSpan();
                respSCtx.setResponseHeader(THttpHeader.TRACE_ID.getKey(), span.getTraceId());
                respSCtx.setResponseHeader(THttpHeader.PARENT_ID.getKey(), span.getParentId());
                respSCtx.setResponseHeader(THttpHeader.SPAN_ID.getKey(), span.getId());
                injectTraceHeaders(respSCtx);
                applyResponseStatus(respSCtx.getTraceData(), respSCtx.getProviderResponse().getStatus());
            }));

    public static final ExtensionBundle TRANSPORT_STATE_MAPPING_BUNDLE = createExtBundle(createCtxBundle(
            (InterceptorExtension<THCExtensionContext>) reqCCtx -> {
            }, (InterceptorExtension<THCExtensionContext>) respCCtx -> {
                int status = respCCtx.getResponseStatus();
                Metadata metadata = respCCtx.getTraceData().getClientSpan().getMetadata();
                metadata.putValue(THMetadataProperties.TH_RESPONSE_STATUS, status);
                metadata.putValue(THMetadataProperties.TH_RESPONSE_MESSAGE, respCCtx.getResponseMessage());

                String errorClassHeaderKey = THttpHeader.ERROR_CLASS.getKey();
                String errorReasonHeaderKey = THttpHeader.ERROR_REASON.getKey();
                THResponseInfo thResponseInfo =
                        new THResponseInfo(status, respCCtx.getResponseHeader(errorClassHeaderKey),
                                respCCtx.getResponseHeader(errorReasonHeaderKey), respCCtx.getResponseMessage());
                WErrorDefinition errorDefinition = THProviderErrorMapper.createErrorDefinition(thResponseInfo, () -> {
                    throw new THRequestInterceptionException(TTransportErrorType.BAD_HEADER, errorClassHeaderKey);
                });

                metadata.putValue(THMetadataProperties.TH_RESPONSE_INFO, thResponseInfo);
                metadata.putValue(MetadataProperties.ERROR_DEFINITION, errorDefinition);
                if (errorDefinition != null && errorDefinition.getErrorType() != WErrorType.BUSINESS_ERROR) {
                    metadata.putValue(MetadataProperties.RESPONSE_SKIP_READING_FLAG, true);
                }
            }), createCtxBundle(
            (InterceptorExtension<THSExtensionContext>) reqSCtx -> {
            }, (InterceptorExtension<THSExtensionContext>) respSCtx -> {
                ContextSpan serviceSpan = respSCtx.getTraceData().getServiceSpan();
                if (serviceSpan.getMetadata().containsKey(THMetadataProperties.TH_TRANSPORT_RESPONSE_SET_FLAG)) {
                    return;
                }
                logIfError(serviceSpan);
                HttpServletResponse response = respSCtx.getProviderResponse();
                if (response.isCommitted()) {
                    log.error("Can't perform response mapping: Transport response is already committed");
                } else {
                    THResponseInfo responseInfo = THProviderErrorMapper.getResponseInfo(serviceSpan);
                    response.setStatus(responseInfo.getStatus());
                    Optional.ofNullable(responseInfo.getErrClass()).ifPresent(val -> {
                        response.setHeader(THttpHeader.ERROR_CLASS.getKey(), val);
                    });
                    Optional.ofNullable(responseInfo.getErrReason()).ifPresent(val -> {
                        response.setHeader(THttpHeader.ERROR_REASON.getKey(), val);
                    });
                    serviceSpan.getMetadata().putValue(THMetadataProperties.TH_TRANSPORT_RESPONSE_SET_FLAG, true);
                    applyResponseStatus(respSCtx.getTraceData(), responseInfo.getStatus());
                }
            }));

    private static final List<ExtensionBundle> clientList = Collections.unmodifiableList(
            Arrays.asList(RPC_ID_BUNDLE, CALL_ENDPOINT_BUNDLE, TRANSPORT_STATE_MAPPING_BUNDLE,
                    TRANSPORT_INJECTION_BUNDLE, DEADLINE_BUNDLE));

    private static final List<ExtensionBundle> serviceList = Collections.unmodifiableList(
            Arrays.asList(TRANSPORT_CONFIG_BUNDLE, RPC_ID_BUNDLE, CALL_ENDPOINT_BUNDLE, TRANSPORT_STATE_MAPPING_BUNDLE,
                    TRANSPORT_INJECTION_BUNDLE, DEADLINE_BUNDLE));

    public static List<ExtensionBundle> getClientExtensions() {
        return clientList;
    }

    public static List<ExtensionBundle> getServiceExtensions() {
        return serviceList;
    }

    public static List<ExtensionBundle> getExtensions(boolean isClient) {
        return isClient ? getClientExtensions() : getServiceExtensions();
    }

    private static TextMapPropagator propagator() {
        return GlobalOpenTelemetry.get().getPropagators().getTextMapPropagator();
    }

    private static void extractTraceContext(THSExtensionContext context) {
        HttpServletRequest request = context.getProviderRequest();
        Context extracted = propagator().extract(Context.root(), request, REQUEST_HEADER_GETTER);
        if (io.opentelemetry.api.trace.Span.fromContext(extracted).getSpanContext().isValid()) {
            context.getTraceData().setInboundTraceParent(request.getHeader(THttpHeader.TRACE_PARENT.getKey()));
            context.getTraceData().setInboundTraceState(request.getHeader(THttpHeader.TRACE_STATE.getKey()));
        } else {
            context.getTraceData().setInboundTraceParent(null);
            context.getTraceData().setInboundTraceState(null);
        }
        context.getTraceData().setPendingParentContext(extracted);
    }

    private static void injectTraceHeaders(THCExtensionContext context) {
        propagator().inject(context.getTraceData().getOtelContext(), context, CLIENT_REQUEST_SETTER);
    }

    private static void injectTraceHeaders(THSExtensionContext context) {
        String traceParent = context.getTraceData().getInboundTraceParent();
        if (traceParent != null && !traceParent.isEmpty()) {
            context.setResponseHeader(THttpHeader.TRACE_PARENT.getKey(), traceParent);
            String traceState = context.getTraceData().getInboundTraceState();
            if (traceState != null && !traceState.isEmpty()) {
                context.setResponseHeader(THttpHeader.TRACE_STATE.getKey(), traceState);
            }
        }
    }

    private static void applyResponseStatus(TraceData traceData, int status) {
        if (status <= 0) {
            return;
        }
        io.opentelemetry.api.trace.Span span = traceData.getOtelSpan();
        if (span == null || !span.getSpanContext().isValid()) {
            return;
        }
        span.setAttribute(HttpAttributes.HTTP_RESPONSE_STATUS_CODE, status);
        span.setStatus(status >= 500 ? StatusCode.ERROR : StatusCode.OK);
    }

    private static void logIfError(ContextSpan contextSpan) {
        Throwable t = ContextUtils.getCallError(contextSpan);
        if (t != null) {
            log.debug("Response has error:", t);
        }
    }

    private static void validateAndProcessTraceHeaders(HttpServletRequest request,
                                                       Function<THttpHeader, String> getHeaderKeyFunction,
                                                       List<Map.Entry<THttpHeader, Consumer<String>>> headerConsumers) {
        if (log.isDebugEnabled()) {
            printHeader(request);
        }
        List<String> missingHeaders = headerConsumers.stream().filter(entry -> {
            String id = Optional.ofNullable(request.getHeader(getHeaderKeyFunction.apply(entry.getKey()))).orElse("");
            entry.getValue().accept(id);
            return id.isEmpty() && !entry.getKey().isOptional();
        }).map(entry -> getHeaderKeyFunction.apply(entry.getKey())).collect(Collectors.toList());

        if (!missingHeaders.isEmpty()) {
            throw new THRequestInterceptionException(TTransportErrorType.BAD_TRACE_HEADER,
                    String.join(", ", missingHeaders));
        }
    }

    private static void printHeader(HttpServletRequest request) {
        Map<String, List<String>> headersMap = Collections.list(request.getHeaderNames()).stream().collect(
                Collectors.toMap(Function.identity(), headerName -> Collections.list(request.getHeaders(headerName))));
        log.debug("Request headers: {}", headersMap);
    }

    private static final TextMapGetter<HttpServletRequest> REQUEST_HEADER_GETTER = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(HttpServletRequest carrier) {
            if (carrier == null) {
                return Collections.emptyList();
            }
            Enumeration<String> headerNames = carrier.getHeaderNames();
            return headerNames == null ? Collections.emptyList() : Collections.list(headerNames);
        }

        @Override
        public String get(HttpServletRequest carrier, String key) {
            if (carrier == null || key == null) {
                return null;
            }
            return carrier.getHeader(key);
        }
    };

    private static final TextMapSetter<THCExtensionContext> CLIENT_REQUEST_SETTER = (carrier, key, value) -> {
        if (carrier != null && key != null && value != null) {
            carrier.setRequestHeader(key, value);
        }
    };
}
