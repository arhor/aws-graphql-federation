package com.github.arhor.aws.graphql.federation.starter.tracing;

import com.github.arhor.aws.graphql.federation.common.constants.Attributes;
import org.slf4j.MDC;

import java.util.UUID;

public final class Utils {

    private Utils() {}

    public static void withExtendedMDC(final UUID traceId, final Runnable block) {
        final var attributeKey = Attributes.TRACE_ID.getKey();
        MDC.put(attributeKey, traceId.toString());
        try {
            block.run();
        } finally {
            MDC.remove(attributeKey);
        }
    }
}
