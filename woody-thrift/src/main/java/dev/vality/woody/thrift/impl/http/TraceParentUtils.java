package dev.vality.woody.thrift.impl.http;

public class TraceParentUtils {

    public static String DEFAULT_VERSION = "00";

    public static String initParentTrace(String version, String trace, String span, String flag) {
        return version + "-" + trace + "-" + span + "-" + flag;
    }

    public static String parseVersion(String parentTraceId) {
        return parentTraceId.split("-").length > 0 ? parentTraceId.split("-")[0] : "";
    }

    public static String parseTraceId(String parentTraceId) {
        return parentTraceId.split("-").length > 1 ? parentTraceId.split("-")[1] : "";
    }

    public static String parseSpanId(String parentTraceId) {
        return parentTraceId.split("-").length > 2 ?  parentTraceId.split("-")[2] : "";
    }

    public static String parseFlag(String parentTraceId) {
        return parentTraceId.split("-").length > 3 ? parentTraceId.split("-")[3] : "";
    }

}
