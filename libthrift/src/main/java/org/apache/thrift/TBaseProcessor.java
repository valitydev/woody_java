package org.apache.thrift;

import java.util.Collections;
import java.util.Map;

import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.MetadataProperties;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.api.trace.context.TraceContext;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;

import static dev.vality.woody.api.trace.context.TraceContext.getCurrentTraceData;

public abstract class TBaseProcessor<I> implements TProcessor {
  private final I iface;
  private final Map<String, ProcessFunction<I, ? extends TBase>> processMap;

  protected TBaseProcessor(
      I iface, Map<String, ProcessFunction<I, ? extends TBase>> processFunctionMap) {
    this.iface = iface;
    this.processMap = processFunctionMap;
  }

  public Map<String, ProcessFunction<I, ? extends TBase>> getProcessMapView() {
    return Collections.unmodifiableMap(processMap);
  }

  @Override
  public void process(TProtocol in, TProtocol out) throws TException {
    TMessage msg = in.readMessageBegin();
    getCurrentTraceData().getServiceSpan().getMetadata().putValue(MetadataProperties.CALL_NAME, msg.name);
    ProcessFunction fn = processMap.get(msg.name);
    if (fn == null) {
      TProtocolUtil.skip(in, TType.STRUCT);
      in.readMessageEnd();
      throw new TApplicationException(
              TApplicationException.UNKNOWN_METHOD, "Invalid method name: '" + msg.name + "'");
    } else {
      fn.process(msg.seqid, in, out, iface);
    }
  }
}
