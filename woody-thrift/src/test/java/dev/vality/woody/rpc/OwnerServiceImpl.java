package dev.vality.woody.rpc;

import org.apache.thrift.TException;

public class OwnerServiceImpl implements OwnerServiceSrv.Iface {
    @Override
    public int getIntValue() throws TException {
        return 0;
    }

    @Override
    public Owner getOwner(int id) throws TException {
        return new Owner(1, "name");
    }

    @Override
    public Owner getErrOwner(int id) throws test_error, TException {
        return null;
    }

    @Override
    public void setOwner(Owner owner) throws TException {

    }

    @Override
    public void setOwnerOneway(Owner owner) throws TException {

    }

    @Override
    public Owner setErrOwner(Owner owner, int id) throws TException {
        return null;
    }
}
