/**
 *
 */
package org.apache.thrift;

import dev.vality.woody.api.event.CallType;
import dev.vality.woody.api.trace.MetadataProperties;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.vality.woody.api.trace.context.TraceContext.getCurrentTraceData;

public abstract class ProcessFunction<I, T extends TBase> {
  private final String methodName;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessFunction.class.getName());

  public ProcessFunction(String methodName) {
    this.methodName = methodName;
  }

  public final void process(int seqid, TProtocol iprot, TProtocol oprot, I iface) throws TException {
    getCurrentTraceData().getServiceSpan().getMetadata().putValue(MetadataProperties.CALL_TYPE, isOneway() ? CallType.CAST : CallType.CALL);
    T args = getEmptyArgsInstance();

    args.read(iprot);
    iprot.readMessageEnd();
    TBase result = null;

    try {
      result = getResult(iface, args);
    } catch(TException ex) {
      LOGGER.error("Internal error processing " + getMethodName(), ex);
      if(rethrowUnhandledExceptions()) throw new RuntimeException(ex.getMessage(), ex);
      throw ex;
    }

    if(!isOneway()) {
      oprot.writeMessageBegin(new TMessage(getMethodName(), TMessageType.REPLY, seqid));
      result.write(oprot);
      oprot.writeMessageEnd();
      oprot.getTransport().flush();
    }
  }

  protected boolean rethrowUnhandledExceptions() {
    return false;
  }

  protected abstract boolean isOneway();

  public abstract TBase getResult(I iface, T args) throws TException;

  public abstract T getEmptyArgsInstance();

  public String getMethodName() {
    return methodName;
  }
}

