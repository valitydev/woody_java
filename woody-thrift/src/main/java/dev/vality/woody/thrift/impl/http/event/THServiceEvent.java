package dev.vality.woody.thrift.impl.http.event;

import dev.vality.woody.api.event.ServiceEvent;
import dev.vality.woody.api.trace.ContextUtils;
import dev.vality.woody.api.trace.TraceData;
import dev.vality.woody.thrift.impl.http.TErrorType;
import dev.vality.woody.thrift.impl.http.THMetadataProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class THServiceEvent extends ServiceEvent {
    public THServiceEvent(TraceData traceData) {
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

    public HttpServletRequest getTransportRequest() {
        return ContextUtils.getMetadataValue(getActiveSpan(), HttpServletRequest.class, THMetadataProperties.TH_TRANSPORT_REQUEST);
    }

    public HttpServletResponse getTransportResponse() {
        return ContextUtils.getMetadataValue(getActiveSpan(), HttpServletResponse.class, THMetadataProperties.TH_TRANSPORT_RESPONSE);
    }
}
