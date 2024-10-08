package dev.vality.woody.thrift.impl.http.interceptor.ext;

import dev.vality.woody.api.event.CompositeClientEventListener;
import dev.vality.woody.api.event.CompositeServiceEventListener;
import dev.vality.woody.api.flow.WFlow;
import dev.vality.woody.api.flow.error.WErrorSource;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.flow.error.WRuntimeException;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.Metadata;
import dev.vality.woody.api.trace.context.TraceContext;
import dev.vality.woody.api.trace.context.metadata.MetadataConversionException;
import dev.vality.woody.api.trace.context.metadata.MetadataConverter;
import dev.vality.woody.api.trace.context.metadata.MetadataExtension;
import dev.vality.woody.api.trace.context.metadata.MetadataExtensionKit;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.thrift.impl.http.AbstractTest;
import dev.vality.woody.thrift.impl.http.OwnerServiceStub;
import jakarta.servlet.Servlet;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TestCustomMetadataExtension extends AbstractTest {

    OwnerServiceSrv.Iface rpcMetaClientToMetaSrv =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                    new CompositeClientEventListener(), Arrays.asList(IntExtension.instance),
                    getUrlString("/rpc_cmeta"));
    OwnerServiceSrv.Iface rpcMetaClientToNoMetaSrv =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                    new CompositeClientEventListener(), Arrays.asList(IntExtension.instance),
                    getUrlString("/rpc_no_cmeta"));
    OwnerServiceSrv.Iface rpcNoMetaClientToMetaSrv =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                    new CompositeClientEventListener(), getUrlString("/rpc_cmeta"));
    OwnerServiceSrv.Iface rpcNoMetaClientToNoMetaSrv =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                    new CompositeClientEventListener(), getUrlString("/rpc_no_cmeta"));
    OwnerServiceSrv.Iface client1 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            new CompositeClientEventListener(), getUrlString("/rpc_no_cmeta"));
    OwnerServiceSrv.Iface client2 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            new CompositeClientEventListener(), getUrlString("/rpc_no_cmeta"));
    OwnerServiceSrv.Iface client3 = createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
            new CompositeClientEventListener(), getUrlString("/rpc_no_cmeta"));
    OwnerServiceSrv.Iface handler = new OwnerServiceStub() {
        @Override
        public Owner getOwner(int id) throws TException {
            switch (id) {
                case 0:
                    assertEquals("A", ContextUtils.getCustomMetadataValue(String.class, "1"));
                    assertNull("A",
                            ContextUtils.getCustomMetadataValue(TraceContext.getCurrentTraceData().getClientSpan(),
                                    String.class, "1"));
                    ContextUtils.setCustomMetadataValue("2", "B");
                    client2.getOwner(1);
                    ContextUtils.setCustomMetadataValue("3", "EC");
                    break;
                case 1:
                    assertEquals("A", ContextUtils.getCustomMetadataValue(String.class, "1"));
                    assertEquals("B", ContextUtils.getCustomMetadataValue(String.class, "2"));
                    assertNull("A",
                            ContextUtils.getCustomMetadataValue(TraceContext.getCurrentTraceData().getClientSpan(),
                                    String.class, "1"));
                    ContextUtils.setCustomMetadataValue("3", "C");
                    client3.getOwner(2);
                    break;
                case 2:
                    assertEquals("A", ContextUtils.getCustomMetadataValue(String.class, "1"));
                    assertEquals("B", ContextUtils.getCustomMetadataValue(String.class, "2"));
                    assertEquals("C", ContextUtils.getCustomMetadataValue(String.class, "3"));
                    assertNull("A",
                            ContextUtils.getCustomMetadataValue(TraceContext.getCurrentTraceData().getClientSpan(),
                                    String.class, "1"));
                    break;
                case 10:
                    assertEquals((Object) 1, ContextUtils.getCustomMetadataValue(IntExtension.instance.getExtension()));
                    break;
                case 11:
                    assertEquals("test", ContextUtils.getCustomMetadataValue(String.class, "test.test.test"));
                    break;
                default:
                    super.getOwner(id);
            }
            return super.getOwner(id);
        }
    };
    Servlet cMetaServlet =
            createThriftRPCService(OwnerServiceSrv.Iface.class, handler, new CompositeServiceEventListener(),
                    Arrays.asList(IntExtension.instance));
    Servlet ncMetaServlet =
            createThriftRPCService(OwnerServiceSrv.Iface.class, handler, new CompositeServiceEventListener());
    Servlet cMetaMultipleExtKitServlet =
            createThriftRPCService(OwnerServiceSrv.Iface.class, handler, new CompositeServiceEventListener(),
                    Arrays.asList(IntExtension.instance, StringExtension.instance));
    OwnerServiceSrv.Iface rpcMetaMultiExtClientToMultiExtMetaSrv =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(),
                    new CompositeClientEventListener(), Arrays.asList(IntExtension.instance, StringExtension.instance),
                    getUrlString("/rpc_cmeta"));

    @Before
    public void setUp() {
    }

    @Test
    public void testIsolation() {
        addServlet(ncMetaServlet, "/rpc_no_cmeta");

        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue("1", "A");
            try {
                assertEquals("A", ContextUtils.getCustomMetadataValue(Object.class, "1"));
                client1.getOwner(0);
                assertEquals("A", ContextUtils.getCustomMetadataValue(Object.class, "1"));
                assertNull(ContextUtils.getCustomMetadataValue(Object.class, "2"));
            } catch (TException e) {
                e.printStackTrace();
                fail();
            }
        }).run();
    }

    @Test
    public void testExtensionMeta() {
        addServlet(cMetaServlet, "/rpc_cmeta");

        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue(1, IntExtension.instance.getExtension());
            try {
                rpcMetaClientToMetaSrv.getOwner(10);
                assertEquals((Object) 1, ContextUtils.getCustomMetadataValue(IntExtension.instance.getExtension()));
            } catch (TException e) {
                e.printStackTrace();
                fail();
            }
        }).run();
    }

    @Test
    public void testCustomExtension() {
        addServlet(cMetaServlet, "/rpc_cmeta");

        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue(IntExtension.KEY, "1");
            try {
                rpcMetaClientToMetaSrv.getOwner(10);
                fail();
            } catch (TException e) {
                e.printStackTrace();
                fail();
            } catch (WRuntimeException e) {
                assertTrue(e.getErrorDefinition().getGenerationSource() == WErrorSource.INTERNAL);
                assertTrue(e.getErrorDefinition().getErrorSource() == WErrorSource.INTERNAL);
                assertTrue(e.getErrorDefinition().getErrorType() == WErrorType.UNEXPECTED_ERROR);
            }
        }).run();
    }

    @Test
    public void testCustomExtensionWithDotSymbol() {
        addServlet(ncMetaServlet, "/rpc_no_cmeta");

        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue("test.test.test", "test");
            try {
                rpcNoMetaClientToNoMetaSrv.getOwner(11);
            } catch (TException e) {
                e.printStackTrace();
                fail();
            }
        }).run();
    }

    @Test
    public void testMultipleExtKit() throws Exception {
        addServlet(cMetaMultipleExtKitServlet, "/rpc_cmeta");
        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue(1, IntExtension.instance.getExtension());
            ContextUtils.setCustomMetadataValue(StringExtension.KEY, "test");
            return rpcMetaMultiExtClientToMultiExtMetaSrv.getIntValue();
        }).call();
    }

    @Test
    public void testCustomExtensionNoData() {
        addServlet(cMetaServlet, "/rpc_cmeta");

        new WFlow().createServiceFork(() -> {
            try {
                rpcMetaClientToMetaSrv.getOwner(10);
                fail();
            } catch (TException e) {
                e.printStackTrace();
                fail();
            } catch (WRuntimeException e) {
                assertTrue(e.getErrorDefinition().getGenerationSource() == WErrorSource.INTERNAL);
                assertTrue(e.getErrorDefinition().getErrorSource() == WErrorSource.INTERNAL);
                assertTrue(e.getErrorDefinition().getErrorType() == WErrorType.PROVIDER_ERROR);
            }
        }).run();
    }

    @Test
    public void testCustomExtensionSrvNoCMeta() {
        addServlet(ncMetaServlet, "/rpc_no_cmeta");

        new WFlow().createServiceFork(() -> {
            ContextUtils.setCustomMetadataValue(IntExtension.KEY, 1);
            try {
                rpcMetaClientToNoMetaSrv.getOwner(-1);
            } catch (TException e) {
                e.printStackTrace();
                fail();
            }
        }).run();
    }

    @Test
    public void testNoCustomExtensionSrvCMeta() {
        addServlet(cMetaServlet, "/rpc_cmeta");

        new WFlow().createServiceFork(() -> {
            try {
                rpcNoMetaClientToMetaSrv.getOwner(-1);
                fail();
            } catch (TException e) {
                e.printStackTrace();
                fail();
            } catch (WRuntimeException e) {
                assertEquals(WErrorSource.INTERNAL, e.getErrorDefinition().getErrorSource());
                assertEquals(WErrorSource.EXTERNAL, e.getErrorDefinition().getGenerationSource());
                assertEquals(WErrorType.UNEXPECTED_ERROR, e.getErrorDefinition().getErrorType());
            }
        }).run();
    }

    static class IntExtension implements MetadataExtensionKit<Integer> {
        static final IntExtension instance = new IntExtension();
        private static final String KEY = "int-val";
        private final boolean applyToObject;
        private final boolean applyToString;

        public IntExtension(boolean applyToObject, boolean applyToString) {
            this.applyToObject = applyToObject;
            this.applyToString = applyToString;
        }

        public IntExtension() {
            this(false, false);
        }

        @Override
        public MetadataExtension<Integer> getExtension() {
            return new MetadataExtension<Integer>() {
                @Override
                public Integer getValue(Metadata metadata) {
                    return metadata.getValue(KEY);
                }

                @Override
                public void setValue(Integer val, Metadata metadata) {
                    metadata.putValue(KEY, val);
                }
            };
        }

        @Override
        public MetadataConverter<Integer> getConverter() {
            return new MetadataConverter<Integer>() {
                @Override
                public Integer convertToObject(String key, String value) throws MetadataConversionException {
                    return Integer.parseInt(value);
                }

                @Override
                public String convertToString(String key, Integer value) throws MetadataConversionException {
                    return String.valueOf(value);
                }

                @Override
                public boolean apply(String key) {
                    return KEY.equalsIgnoreCase(key);
                }

                @Override
                public boolean applyToObject() {
                    return IntExtension.this.applyToObject;
                }

                @Override
                public boolean applyToString() {
                    return IntExtension.this.applyToString;
                }
            };
        }
    }

    static class StringExtension implements MetadataExtensionKit<String> {
        static final StringExtension instance = new StringExtension();
        private static final String KEY = "string-val";
        private final boolean applyToObject;
        private final boolean applyToString;

        public StringExtension(boolean applyToObject, boolean applyToString) {
            this.applyToObject = applyToObject;
            this.applyToString = applyToString;
        }

        public StringExtension() {
            this(false, false);
        }

        @Override
        public MetadataExtension<String> getExtension() {
            return new MetadataExtension<String>() {
                @Override
                public String getValue(Metadata metadata) {
                    return metadata.getValue(KEY);
                }

                @Override
                public void setValue(String val, Metadata metadata) {
                    metadata.putValue(KEY, val);
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
                    return KEY.equalsIgnoreCase(key);
                }

                @Override
                public boolean applyToObject() {
                    return StringExtension.this.applyToObject;
                }

                @Override
                public boolean applyToString() {
                    return StringExtension.this.applyToString;
                }
            };
        }
    }

}
