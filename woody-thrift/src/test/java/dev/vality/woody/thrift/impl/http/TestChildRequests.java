package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.api.event.ClientEventListener;
import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorSource;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.flow.error.WRuntimeException;
import dev.vality.woody.api.generator.TimestampIdGenerator;
import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.Servlet;

import static java.lang.System.out;

public class TestChildRequests extends AbstractTest {


    ClientEventListener clientListener = null;
    OwnerServiceSrv.Iface client1 =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(), clientListener,
                    getUrlString("/rpc"));
    OwnerServiceSrv.Iface client2 =
            createThriftRPCClient(OwnerServiceSrv.Iface.class, new TimestampIdGenerator(), clientListener,
                    getUrlString("/rpc"));
    OwnerServiceSrv.Iface handler = new OwnerServiceStub() {

        @Override
        public void setOwnerOneway(Owner owner) throws TException {
            if (owner.getId() == 0) {
                client2.setOwnerOneway(new Owner(1, ""));
            }
        }

        @Override
        public Owner getErrOwner(int id) throws TException {
            switch (id) {
                case 0:
                    Owner owner = client2.getOwner(0);
                    client2.setOwnerOneway(owner);
                    return client2.getOwner(10);
                case 200:
                    throw new test_error(200);
                case 500:
                    throw new RuntimeException("Test");
                default:
                    return super.getErrOwner(id);
            }
        }
    };

    Servlet servlet = createThriftRPCService(OwnerServiceSrv.Iface.class, handler, null);

    @Before
    public void before() {
        addServlet(servlet, "/rpc");
    }

    @Test
    public void testEventOrder() throws TException {

        out.println("Root call>");
        client1.setOwnerOneway(new Owner(0, ""));
        out.println("<");
        out.println("Root call>");

        out.println("Root call>");
        Assert.assertEquals(new Owner(10, "10"), client1.getErrOwner(0));
        out.println("<");

        out.println("Root call>");
        try {
            client1.getErrOwner(200);
            Assert.fail();
        } catch (test_error ignored) {
            //ignore
        }
        out.println("<");
        out.println("Root call>");
        try {
            client1.getErrOwner(500);
        } catch (WRuntimeException e) {
            WErrorDefinition errorDefinition = new WErrorDefinition(WErrorSource.EXTERNAL);
            errorDefinition.setErrorSource(WErrorSource.INTERNAL);
            errorDefinition.setErrorType(WErrorType.UNEXPECTED_ERROR);
            errorDefinition.setErrorReason("RuntimeException:Test");
            errorDefinition.setErrorMessage("Server Error");
            Assert.assertEquals(errorDefinition, e.getErrorDefinition());
        }
        out.println("<");


    }

}
