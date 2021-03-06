package dev.vality.woody.thrift.impl.http;

import dev.vality.woody.rpc.Owner;
import dev.vality.woody.rpc.OwnerServiceSrv;
import dev.vality.woody.rpc.test_error;
import org.apache.thrift.TException;

public class OwnerServiceStub implements OwnerServiceSrv.Iface {
    @Override
    public int getIntValue() throws TException {
        return 0;
    }

    @Override
    public Owner getOwner(int id) throws TException {
        return new Owner(id, "" + id);
    }

    @Override
    public Owner getErrOwner(int id) throws test_error, TException {
        throw new test_error(id);
    }

    @Override
    public void setOwner(Owner owner) throws TException {

    }

    @Override
    public void setOwnerOneway(Owner owner) throws TException {

    }

    @Override
    public Owner setErrOwner(Owner owner, int id) throws TException {
        throw new test_error(id);
    }
}
