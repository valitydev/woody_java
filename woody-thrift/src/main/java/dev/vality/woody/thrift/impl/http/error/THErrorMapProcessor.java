package dev.vality.woody.thrift.impl.http.error;

import dev.vality.woody.api.flow.error.ErrorMapProcessor;
import dev.vality.woody.api.flow.error.WErrorMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class THErrorMapProcessor extends ErrorMapProcessor {

    public THErrorMapProcessor(boolean isClient, List<WErrorMapper> mappers) {
        super(isClient, mappers);
    }

    private static List<WErrorMapper> getMappers(Class ifaceClass) {
        return Arrays.asList(new THBusinessErrorMapper(ifaceClass), new THSystemErrorMapper(),
                new THTransportErrorMapper(), new THProviderErrorMapper(), new THDefaultErrorMapper());
    }

    public static THErrorMapProcessor getInstance(boolean isClient, Class ifaceClass, WErrorMapper customMapper) {
        if (customMapper != null) {
            return new THErrorMapProcessor(isClient, new ArrayList(getMappers(ifaceClass)) {
                {
                    add(0, customMapper);
                }
            });
        } else {
            return getInstance(isClient, ifaceClass);
        }
    }

    public static THErrorMapProcessor getInstance(boolean isClient, Class ifaceClass) {
        return new THErrorMapProcessor(isClient, getMappers(ifaceClass));
    }
}
