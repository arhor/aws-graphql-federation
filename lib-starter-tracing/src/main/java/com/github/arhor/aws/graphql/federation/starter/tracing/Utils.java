package com.github.arhor.aws.graphql.federation.starter.tracing;

import org.slf4j.MDC;

import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.starter.tracing.AttributesKt.TRACING_ID_KEY;

public final class Utils {

    private Utils() {
    }

    public static void withExtendedMDC(final UUID traceId, final Runnable block) {
        MDC.put(TRACING_ID_KEY, traceId.toString());
        try {
            block.run();
        } finally {
            MDC.remove(TRACING_ID_KEY);
        }
    }
}
