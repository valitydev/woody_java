package dev.vality.woody.thrift.impl.http.event;

import dev.vality.woody.api.event.ClientEventListener;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class HttpClientEventLogListener implements ClientEventListener<THClientEvent> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    @Override
    public void notifyEvent(THClientEvent event) {
        try {

            switch (event.getEventType()) {
                case CLIENT_SEND:
                    HttpUriRequest httpRequest = event.getTransportRequest();
                    if (httpRequest != null) {
                        log.info("CLIENT Event: {}, {}", event.getEventType(), buildRequestLog(httpRequest));
                    }
                    break;
                case CLIENT_RECEIVE:
                    HttpResponse httpResponse = event.getTransportResponse();
                    if (httpResponse != null) {
                        log.info("CLIENT Event: {}, {}", event.getEventType(), buildResponseLog(httpResponse));
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("Event processing failed", e);
        }
    }

    private String buildRequestLog(HttpUriRequest requestBase) {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpRequest:").append(requestBase.toString()).append(", Headers:")
                .append(Arrays.toString(requestBase.getHeaders()));

        return sb.toString();

    }

    private String buildResponseLog(HttpResponse httpResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpResponse:").append(httpResponse.getReasonPhrase()).append(", Headers:")
                .append(Arrays.toString(httpResponse.getHeaders()));

        return sb.toString();
    }
}
