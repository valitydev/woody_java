package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.flow.WFlow;
import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorSource;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.flow.error.WRuntimeException;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.Metadata;
import dev.vality.woody.api.trace.context.metadata.MetadataConversionException;
import dev.vality.woody.api.trace.context.metadata.MetadataConverter;
import dev.vality.woody.api.trace.context.metadata.MetadataExtension;
import dev.vality.woody.api.trace.context.metadata.MetadataExtensionKit;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import dev.vality.woody.thrift.impl.http.transport.THttpHeader;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.thrift.TException;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class TestClientAndServerHttpHeaders extends AbstractTest {

    private final String servletContextPath = "/test_servlet";

    private final Servlet testServlet =
            createThriftRPCService(OwnerServiceSrv.Iface.class, new OwnerServiceSrv.Iface() {

                @Override
                public int getIntValue() throws TException {
                    return 42;
                }

                @Override
                public Owner getOwner(int id) throws TException {
                    return new Owner(id, "test");
                }

                @Override
                public Owner getErrOwner(int id) throws TException {
                    throw new test_error();
                }

                @Override
                public void setOwner(Owner owner) throws TException {
                    //nothing
                }

                @Override
                public void setOwnerOneway(Owner owner) throws TException {
                    //nothing
                }

                @Override
                public Owner setErrOwner(Owner owner, int id) throws TException {
                    throw new test_error();
                }
            });

    @Test
    public void testCheckClientTraceHeaders() throws TException {
        addServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                for (THttpHeader tHttpHeader : Arrays.asList(THttpHeader.SPAN_ID, THttpHeader.TRACE_ID,
                        THttpHeader.PARENT_ID)) {
                    assertNotNull(request.getHeader(tHttpHeader.getKey()));
                }
                writeResultMessage(request, response);
            }
        }, "/check_trace_headers");
        OwnerServiceSrv.Iface client =
                createThriftRPCClient(OwnerServiceSrv.Iface.class, getUrlString("/check_trace_headers"));
        client.getIntValue();
    }

    @Test
    public void testCheckClientMetaHeaders() {
        Map.Entry<String, String> metadataWithoutExtensionKit = new AbstractMap.SimpleEntry<>("test", "test-value");
        Map.Entry<String, String> metadataWithExtensionKit =
                new AbstractMap.SimpleEntry<>("extension-test", "extension-test-value");
        MetadataExtensionKit metadataExtensionTestKit = new MetadataExtensionKit<String>() {

            @Override
            public MetadataExtension<String> getExtension() {
                return new MetadataExtension<String>() {
                    @Override
                    public String getValue(Metadata metadata) {
                        return metadata.getValue(metadataWithExtensionKit.getKey());
                    }

                    @Override
                    public void setValue(String val, Metadata metadata) {
                        metadata.putValue(metadataWithExtensionKit.getKey(), val);
                    }
                };
            }

            @Override
            public MetadataConverter<String> getConverter() {
                return new MetadataConverter<String>() {
                    @Override
                    public String convertToObject(String key, String value) throws MetadataConversionException {
                        return value;
                    }

                    @Override
                    public String convertToString(String key, String value) throws MetadataConversionException {
                        return value;
                    }

                    @Override
                    public boolean apply(String key) {
                        return metadataWithExtensionKit.getKey().equalsIgnoreCase(key);
                    }
                };
            }
        };

        addServlet(new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                assertEquals(request.getHeader(THttpHeader.META.getKey() + metadataWithExtensionKit.getKey()),
                        metadataWithExtensionKit.getValue());
                assertEquals(request.getHeader(THttpHeader.META.getKey() + metadataWithoutExtensionKit.getKey()),
                        metadataWithoutExtensionKit.getValue());
                writeResultMessage(request, response);
            }
        }, "/check_meta_headers");
        OwnerServiceSrv.Iface client =
                createThriftRPCClient(OwnerServiceSrv.Iface.class, Arrays.asList(metadataExtensionTestKit),
                        getUrlString("/check_meta_headers"));

        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue(metadataWithoutExtensionKit.getKey(),
                    metadataWithoutExtensionKit.getValue());
            ContextUtils.setCustomMetadataValue(metadataWithExtensionKit.getKey(), metadataWithExtensionKit.getValue());
            try {
                client.getIntValue();
            } catch (TException e) {
                e.printStackTrace();
                fail();
            }
        }).run();
    }

    @Test
    public void testWhenTraceDataIsEmpty() throws TException {
        addServlet(testServlet, servletContextPath);
        CloseableHttpClient httpClient =
                HttpClients.custom().addRequestInterceptorFirst((httpRequest, entityDetails, httpContext) -> {
                    httpRequest.removeHeader(httpRequest.getFirstHeader(THttpHeader.SPAN_ID.getKey()));
                    httpRequest.removeHeader(httpRequest.getFirstHeader(THttpHeader.TRACE_ID.getKey()));
                    httpRequest.removeHeader(httpRequest.getFirstHeader(THttpHeader.PARENT_ID.getKey()));
                }).build();

        OwnerServiceSrv.Iface client =
                createThriftRPCClient(OwnerServiceSrv.Iface.class, getUrlString(servletContextPath), httpClient);
        try {
            client.getIntValue();
            fail();
        } catch (WRuntimeException ex) {
            WErrorDefinition errorDefinition = ex.getErrorDefinition();
            assertEquals(WErrorSource.EXTERNAL, errorDefinition.getGenerationSource());
            assertEquals(WErrorType.UNEXPECTED_ERROR, errorDefinition.getErrorType());
            assertEquals(WErrorSource.INTERNAL, errorDefinition.getErrorSource());
            assertEquals("Bad Request", errorDefinition.getErrorMessage());
        }
    }

    @Test
    public void testTraceDataOtel() throws TException {
        addServlet(testServlet, servletContextPath);
        CloseableHttpClient httpClient =
                HttpClients.custom().addRequestInterceptorFirst((httpRequest, entityDetails, httpContext) ->
                                httpRequest.setHeader(
                                        THttpHeader.TRACE_PARENT.getKey(),
                                        "00-80e1afed08e019fc1110464cfa66635c-7a085853722dc6d2-01"
                                )
                        )
                        .addResponseInterceptorLast((httpResponse, entityDetails, httpContext) ->
                                assertEquals(
                                        "00-80e1afed08e019fc1110464cfa66635c-7a085853722dc6d2-01",
                                        httpResponse.getHeader(THttpHeader.TRACE_PARENT.getKey()).getValue()
                                )
                        )
                        .build();

        OwnerServiceSrv.Iface client = createThriftRPCClient(
                OwnerServiceSrv.Iface.class, getUrlString(servletContextPath), httpClient
        );
        client.getIntValue();
    }

    @Test
    public void testWhenTraceDataOtelIsEmpty() throws TException {
        addServlet(testServlet, servletContextPath);
        CloseableHttpClient httpClient =
                HttpClients.custom().addRequestInterceptorFirst((httpRequest, entityDetails, httpContext) ->
                                assertNull(httpRequest.getHeader(THttpHeader.TRACE_PARENT.getKey()))
                        )
                        .addResponseInterceptorLast((httpResponse, entityDetails, httpContext) ->
                                assertNull(httpResponse.getHeader(THttpHeader.TRACE_PARENT.getKey()))
                        )
                        .build();

        OwnerServiceSrv.Iface client = createThriftRPCClient(
                OwnerServiceSrv.Iface.class, getUrlString(servletContextPath), httpClient
        );
        client.getIntValue();
    }

    @Test
    public void testWhenTraceDataOtelIsInvalid() throws TException {
        addServlet(testServlet, servletContextPath);
        CloseableHttpClient httpClient =
                HttpClients.custom().addRequestInterceptorFirst((httpRequest, entityDetails, httpContext) ->
                                httpRequest.setHeader(
                                        THttpHeader.TRACE_PARENT.getKey(),
                                        "invalid"
                                )
                        )
                        .addResponseInterceptorLast((httpResponse, entityDetails, httpContext) ->
                                assertNull(httpResponse.getHeader(THttpHeader.TRACE_PARENT.getKey()))
                        )
                        .build();

        OwnerServiceSrv.Iface client = createThriftRPCClient(
                OwnerServiceSrv.Iface.class, getUrlString(servletContextPath), httpClient
        );
        client.getIntValue();
    }

}
