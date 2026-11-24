package com.infy.scm360.common.correlation;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CorrelationFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String correlationId = MDC.get(CorrelationContext.MDC_CORRELATION_ID);
        if (correlationId != null) {
            template.header(CorrelationContext.CORRELATION_ID_HEADER, correlationId);
        }
        String userId = MDC.get(CorrelationContext.MDC_USER_ID);
        if (userId != null) {
            template.header(CorrelationContext.USER_ID_HEADER, userId);
        }
    }
}
