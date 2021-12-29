package dev.vality.woody.thrift.impl.http.event;

import dev.vality.woody.api.event.ClientEvent;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.thrift.impl.http.TErrorType;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class THClientEvent extends ClientEvent {
    public THClientEvent(TraceData traceData) {
        super(traceData);
    }

    public Integer getThriftCallMsgType() {
        return getActiveSpan().getMetadata().getValue(THMetadataProperties.TH_CALL_MSG_TYPE);
    }

    public Integer getThiftCallResultMsgType() {
        return getActiveSpan().getMetadata().getValue(THMetadataProperties.TH_CALL_RESULT_MSG_TYPE);
    }

    public TErrorType getThriftErrorType() {
        return getActiveSpan().getMetadata().getValue(THMetadataProperties.TH_ERROR_TYPE);
    }

    public Integer getThriftResponseStatus() {
        return getActiveSpan().getMetadata().getValue(THMetadataProperties.TH_RESPONSE_STATUS);
    }

    public String getThriftResponseMessage() {
        return getActiveSpan().getMetadata().getValue(THMetadataProperties.TH_RESPONSE_MESSAGE);
    }

    public HttpRequestBase getTransportRequest() {
        return ContextUtils.getMetadataValue(getActiveSpan(), HttpRequestBase.class,
                THMetadataProperties.TH_TRANSPORT_REQUEST);
    }

    public HttpResponse getTransportResponse() {
        return ContextUtils.getMetadataValue(getActiveSpan(), HttpResponse.class,
                THMetadataProperties.TH_TRANSPORT_RESPONSE);
    }
}
