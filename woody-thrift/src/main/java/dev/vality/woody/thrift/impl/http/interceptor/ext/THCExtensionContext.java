package dev.vality.woody.thrift.impl.http.interceptor.ext;

import dev.vality.woody.api.interceptor.ext.ExtensionContext;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class THCExtensionContext extends ExtensionContext {
    private static final int REQ_URL_CONNECTION_TYPE = 1;
    private static final int REQ_HTTP_CLIENT_TYPE = 2;
    private static final int RESP_URL_CONNECTION_TYPE = 3;
    private static final int RESP_HTTP_CLIENT_TYPE = 4;

    private int reqContextType = 0;
    private int respContextType = 0;

    public THCExtensionContext(TraceData traceData, Object providerContext, Object[] contextParameters) {
        super(traceData, providerContext, contextParameters);
    }

    public void setRequestHeader(String key, String value) {
        Object providerContext = getProviderContext();
        switch (getRequestContextType(providerContext)) {
            case REQ_URL_CONNECTION_TYPE:
                ((HttpURLConnection) providerContext).setRequestProperty(key, value);
                break;
            case REQ_HTTP_CLIENT_TYPE:
                ((HttpUriRequestBase) providerContext).setHeader(key, value);
                break;
            default:
                throw new RuntimeException("Unknown type:" + providerContext.getClass());
        }
    }

    /**
     * @return the last header value for multi value case, null if header is not found
     */
    public String getResponseHeader(String key) {
        Object providerContext = getProviderContext();
        switch (getResponseContextType(providerContext)) {
            case RESP_URL_CONNECTION_TYPE:
                return ((HttpURLConnection) providerContext).getHeaderField(key);
            case RESP_HTTP_CLIENT_TYPE:
                Header header = ((HttpResponse) providerContext).getLastHeader(key);
                return header == null ? null : header.getValue();
            default:
                throw new RuntimeException("Unknown type:" + providerContext.getClass());
        }
    }

    public Collection<String> getResponseHeaderKeys() {
        Object providerContext = getProviderContext();
        switch (getResponseContextType(providerContext)) {
            case RESP_URL_CONNECTION_TYPE:
                return ((HttpURLConnection) providerContext).getHeaderFields().keySet();
            case RESP_HTTP_CLIENT_TYPE:
                return Arrays.stream(((ClassicHttpResponse) providerContext).getHeaders()).map(NameValuePair::getName)
                        .collect(Collectors.toSet());
            default:
                throw new RuntimeException("Unknown type:" + providerContext.getClass());
        }
    }

    public int getResponseStatus() {
        Object providerContext = getProviderContext();
        try {
            switch (getResponseContextType(providerContext)) {
                case RESP_URL_CONNECTION_TYPE:
                    return ((HttpURLConnection) providerContext).getResponseCode();
                case RESP_HTTP_CLIENT_TYPE:
                    return ((ClassicHttpResponse) providerContext).getCode();
                default:
                    throw new RuntimeException("Unknown type:" + providerContext.getClass());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResponseMessage() {
        Object providerContext = getProviderContext();
        try {
            switch (getResponseContextType(providerContext)) {
                case RESP_URL_CONNECTION_TYPE:
                    return ((HttpURLConnection) providerContext).getResponseMessage();
                case RESP_HTTP_CLIENT_TYPE:
                    return ((ClassicHttpResponse) providerContext).getReasonPhrase();
                default:
                    throw new RuntimeException("Unknown type:" + providerContext.getClass());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getRequestCallEndpoint() {
        Object providerContext = getProviderContext();
        switch (getRequestContextType(providerContext)) {
            case REQ_URL_CONNECTION_TYPE:
                return ((HttpURLConnection) providerContext).getURL();
            case REQ_HTTP_CLIENT_TYPE:
                return ContextUtils.getContextValue(URL.class, getContextParameters(), 0);
            default:
                throw new RuntimeException("Unknown type:" + providerContext.getClass());
        }
    }


    private int getRequestContextType(Object providerContext) {
        if (reqContextType == 0) {
            if (providerContext instanceof HttpUriRequestBase) {
                reqContextType = REQ_HTTP_CLIENT_TYPE;
            } else if (providerContext instanceof HttpURLConnection) {
                reqContextType = REQ_URL_CONNECTION_TYPE;
            } else {
                reqContextType = -1;
            }
        }
        return reqContextType;
    }

    private int getResponseContextType(Object providerContext) {
        if (respContextType == 0) {
            if (providerContext instanceof HttpResponse) {
                respContextType = RESP_HTTP_CLIENT_TYPE;
            } else if (providerContext instanceof HttpURLConnection) {
                respContextType = RESP_URL_CONNECTION_TYPE;
            } else {
                respContextType = -1;
            }
        }
        return respContextType;
    }
}
