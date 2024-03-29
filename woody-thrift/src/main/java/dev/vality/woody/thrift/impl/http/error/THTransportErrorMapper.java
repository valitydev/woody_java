package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.flow.error.WErrorMapper;
import dev.vality.woody.api.flow.error.WErrorSource;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.trace.ContextSpan;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

public class THTransportErrorMapper implements WErrorMapper {
    private static final ErrorAnalyzer[] analyzers = new ErrorAnalyzer[] {
            new ErrorAnalyzer(Pattern.compile(SocketTimeoutException.class.getName()),
                    THTransportErrorMapper::getUndefinedResult),
            new ErrorAnalyzer(
                    Pattern.compile(Pattern.quote(org.apache.hc.core5.http.NoHttpResponseException.class.getName())),
                    THTransportErrorMapper::genUnavailableResult),
            new ErrorAnalyzer(Pattern.compile(Pattern.quote(java.net.UnknownHostException.class.getName())),
                    THTransportErrorMapper::genUnavailableResult),
            new ErrorAnalyzer(Pattern.compile(Pattern.quote(java.net.ConnectException.class.getName())),
                    THTransportErrorMapper::genUnavailableResult),
            new ErrorAnalyzer(Pattern.compile("java\\.net\\.Socket\\..*"),
                    THTransportErrorMapper::genUnavailableResult),
            new ErrorAnalyzer(Pattern.compile("java\\.net\\..*"), THTransportErrorMapper::getUndefinedResult),};

    private static WErrorDefinition genUnavailableResult(Throwable t, ContextSpan c) {
        WErrorDefinition def = new WErrorDefinition(WErrorSource.EXTERNAL);
        def.setErrorSource(WErrorSource.INTERNAL);
        def.setErrorType(WErrorType.UNAVAILABLE_RESULT);
        def.setErrorReason(t.toString());
        def.setErrorMessage(t.getMessage());
        return def;
    }

    private static WErrorDefinition getUndefinedResult(Throwable t, ContextSpan c) {
        WErrorDefinition def = new WErrorDefinition(WErrorSource.EXTERNAL);
        def.setErrorSource(WErrorSource.INTERNAL);
        def.setErrorType(WErrorType.UNDEFINED_RESULT);
        def.setErrorReason(t.toString());
        def.setErrorMessage(t.getMessage());
        return def;
    }

    @Override
    public WErrorDefinition mapToDef(Throwable t, ContextSpan contextSpan) {
        return ErrorStackAnalyzer.analyzeStack(analyzers, t, contextSpan);
    }

    @Override
    public Exception mapToError(WErrorDefinition errorDefinition, ContextSpan contextSpan) {
        return null;
    }

}
