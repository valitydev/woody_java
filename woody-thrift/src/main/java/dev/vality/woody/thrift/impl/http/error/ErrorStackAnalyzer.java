package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.WErrorDefinition;
import dev.vality.woody.api.trace.ContextSpan;

public class ErrorStackAnalyzer {
    public static WErrorDefinition analyzeStack(ErrorAnalyzer[] analyzers, Throwable err, ContextSpan contextSpan) {
        Throwable stackErr = err;
        do {
            for (int i = 0; i < analyzers.length; ++i) {
                if (analyzers[i].getPattern().matcher(stackErr.getClass().getName()).matches()) {
                    return analyzers[i].getAnalyzer().apply(stackErr, contextSpan);
                }
            }
            stackErr = stackErr.getCause();
        } while (stackErr != null && stackErr.getCause() != stackErr);
        return null;
    }
}
