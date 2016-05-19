package com.rbkmoney.woody.thrift.impl.http.interceptor;

import com.rbkmoney.woody.api.interceptor.RequestInterceptor;
import com.rbkmoney.woody.api.trace.*;
import com.rbkmoney.woody.thrift.impl.http.THMetadataProperties;
import com.rbkmoney.woody.thrift.impl.http.transport.THttpHeader;
import com.rbkmoney.woody.thrift.impl.http.transport.UrlStringEndpoint;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vpankrashkin on 29.04.16.
 */
public class THCRequestInterceptor implements RequestInterceptor {
    @Override
    public boolean interceptRequest(TraceData traceData, Object providerContext, Object... contextParams) {
        if (providerContext instanceof HttpRequestBase) {
            return interceptRequestBase(traceData.getClientSpan(), (HttpRequestBase) providerContext, contextParams);
        } else if (providerContext instanceof HttpURLConnection) {
            return interceptUrlConnection(traceData.getClientSpan(), (HttpURLConnection) providerContext, contextParams);
        }
        return interceptError(traceData, "Unknown type:" + providerContext.getClass());
    }

    private boolean interceptUrlConnection(ClientSpan clientSpan, HttpURLConnection connection, Object... contextParams) {
        extendMetadata(clientSpan, connection.getURL().toString(), connection);
        ArrayList<String[]> headers = prepareClientHeaders(clientSpan);
        for (int i = 0; i < headers.size(); ++i) {
            connection.setRequestProperty(headers.get(i)[0], headers.get(i)[1]);
        }
        return true;
    }

    protected boolean interceptRequestBase(ClientSpan clientSpan, HttpRequestBase requestBase, Object... contextParams) {
        URL url = ContextUtils.getContextParameter(URL.class, contextParams, 0);
        extendMetadata(clientSpan, url == null ? null : url.toString(), requestBase);
        ArrayList<String[]> headers = prepareClientHeaders(clientSpan);
        for (int i = 0; i < headers.size(); ++i) {
            requestBase.setHeader(headers.get(i)[0], headers.get(i)[1]);
        }
        return true;
    }

    private void extendMetadata(ClientSpan clientSpan, String url, Object request) {
        clientSpan.getMetadata().putValue(MetadataProperties.CALL_ENDPOINT, new UrlStringEndpoint(url));
        clientSpan.getMetadata().putValue(THMetadataProperties.TH_TRANSPORT_REQUEST, request);
    }

    private ArrayList<String[]> prepareClientHeaders(ClientSpan clientSpan) {
        Span span = clientSpan.getSpan();
        ArrayList<String[]> headers = new ArrayList<>();
        headers.add(new String[]{THttpHeader.TRACE_ID.getKeyValue(), span.getTraceId()});
        headers.add(new String[]{THttpHeader.SPAN_ID.getKeyValue(), span.getId()});
        headers.add(new String[]{THttpHeader.PARENT_ID.getKeyValue(), span.getParentId()});

        return headers;
    }

    private boolean interceptError(TraceData traceData, String message) {
        ContextUtils.setInterceptionError(traceData.getClientSpan(), new RuntimeException(message));
        return false;
    }

}
