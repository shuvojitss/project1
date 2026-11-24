package com.infy.scm360.common.correlation;

import org.slf4j.MDC;

public final class CorrelationContext {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLES_HEADER = "X-User-Roles";
    public static final String LOCATION_IDS_HEADER = "X-Location-Ids";

    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_SERVICE = "service";

    private CorrelationContext() {}

    public static String getCorrelationId() {
        String id = MDC.get(MDC_CORRELATION_ID);
        return id != null ? id : "N/A";
    }

    public static void setCorrelationId(String correlationId) {
        MDC.put(MDC_CORRELATION_ID, correlationId);
    }

    public static void clear() {
        MDC.clear();
    }
}
