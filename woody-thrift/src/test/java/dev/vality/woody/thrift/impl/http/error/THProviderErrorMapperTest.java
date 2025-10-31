package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorSource;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.trace.ContextSpan;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;
import dev.vality.woody.thrift.impl.http.THResponseInfo;
import dev.vality.woody.thrift.impl.http.interceptor.THRequestInterceptionException;
import dev.vality.woody.thrift.impl.http.transport.TTransportErrorType;
import org.apache.thrift.transport.TTransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class THProviderErrorMapperTest {

    private TraceData originalTraceData;
    private TraceData testTraceData;

    @Before
    public void setUp() {
        originalTraceData = TraceContext.getCurrentTraceData();
        testTraceData = new TraceData();
        fillServiceSpan(testTraceData.getServiceSpan());
        TraceContext.setCurrentTraceData(testTraceData);
    }

    @After
    public void tearDown() {
        if (testTraceData != null) {
            testTraceData.getOtelSpan().end();
        }
        TraceContext.setCurrentTraceData(originalTraceData);
    }

    @Test
    public void createErrorDefinitionDefaultsToStandardMessage() {
        THResponseInfo responseInfo = new THResponseInfo(502, null, "gateway issue", "");

        WErrorDefinition definition = THProviderErrorMapper.createErrorDefinition(responseInfo, () -> null);

        assertNotNull(definition);
        assertEquals("Bad Request", definition.getErrorMessage());
        assertEquals(WErrorType.UNEXPECTED_ERROR, definition.getErrorType());
        assertEquals(WErrorSource.EXTERNAL, definition.getGenerationSource());
        assertEquals(WErrorSource.EXTERNAL, definition.getErrorSource());
    }

    @Test
    public void createErrorDefinitionAssignsErrorSourceByStatus() {
        THResponseInfo businessInfo = new THResponseInfo(200, WErrorType.BUSINESS_ERROR.getKey(), "business", "");
        WErrorDefinition businessDefinition = THProviderErrorMapper.createErrorDefinition(businessInfo, () -> null);

        assertNotNull(businessDefinition);
        assertEquals(WErrorSource.EXTERNAL, businessDefinition.getGenerationSource());
        assertEquals(WErrorSource.INTERNAL, businessDefinition.getErrorSource());

        THResponseInfo unavailableInfo = new THResponseInfo(503, null, "retry later");
        WErrorDefinition unavailableDefinition =
                THProviderErrorMapper.createErrorDefinition(unavailableInfo, () -> null);

        assertNotNull(unavailableDefinition);
        assertEquals(WErrorSource.EXTERNAL, unavailableDefinition.getGenerationSource());
        assertEquals(WErrorSource.INTERNAL, unavailableDefinition.getErrorSource());
    }

    @Test
    public void interceptionErrorOverridesStatusWithTransportMapping() {
        ContextSpan serviceSpan = testTraceData.getServiceSpan();
        ContextUtils.setInterceptionError(serviceSpan,
                new THRequestInterceptionException(TTransportErrorType.BAD_CONTENT_TYPE, "Content-Type"));

        THResponseInfo responseInfo = THProviderErrorMapper.getResponseInfo(serviceSpan);

        assertEquals(415, responseInfo.getStatus());
        assertNull(responseInfo.getErrClass());
    }

    @Test
    public void transportExceptionWithoutPayloadMarkedAsExternal() {
        ContextSpan serviceSpan = testTraceData.getServiceSpan();
        THProviderErrorMapper mapper = new THProviderErrorMapper();

        WErrorDefinition definition = mapper.mapToDef(new TTransportException("HTTP response code: 502"), serviceSpan);

        assertNotNull(definition);
        assertEquals(WErrorSource.EXTERNAL, definition.getGenerationSource());
        assertEquals(WErrorSource.EXTERNAL, definition.getErrorSource());
        assertEquals(WErrorType.PROVIDER_ERROR, definition.getErrorType());
        assertEquals(dev.vality.woody.thrift.impl.http.TErrorType.TRANSPORT,
                serviceSpan.getMetadata().getValue(THMetadataProperties.TH_ERROR_TYPE));
    }

    @Test
    public void requestInterceptionErrorSetsSubtypeAndSource() {
        ContextSpan serviceSpan = testTraceData.getServiceSpan();
        THProviderErrorMapper mapper = new THProviderErrorMapper();

        WErrorDefinition definition = mapper.mapToDef(
                new THRequestInterceptionException(TTransportErrorType.BAD_HEADER, "X-Test"), serviceSpan);

        assertNotNull(definition);
        assertEquals(WErrorSource.EXTERNAL, definition.getGenerationSource());
        assertEquals(WErrorSource.EXTERNAL, definition.getErrorSource());
        assertEquals("bad header: X-Test", definition.getErrorReason());
        assertEquals(TTransportErrorType.BAD_HEADER,
                serviceSpan.getMetadata().getValue(THMetadataProperties.TH_ERROR_SUBTYPE));
    }

    private void fillServiceSpan(dev.vality.woody.api.trace.ServiceSpan serviceSpan) {
        serviceSpan.getSpan().setTraceId("trace");
        serviceSpan.getSpan().setParentId("parent");
        serviceSpan.getSpan().setId("span");
    }
}
