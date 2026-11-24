package com.infy.scm360.common.config;

import com.infy.scm360.common.correlation.CorrelationFeignInterceptor;
import com.infy.scm360.common.correlation.CorrelationIdFilter;
import com.infy.scm360.common.error.GlobalExceptionHandler;
import com.infy.scm360.common.idempotency.IdempotentConsumer;
import com.infy.scm360.common.messaging.RabbitMqConfig;
import com.infy.scm360.common.outbox.OutboxRelay;
import com.infy.scm360.common.outbox.OutboxService;
import com.infy.scm360.common.security.CommonSecurityConfig;
import com.infy.scm360.common.security.JwtAuthenticationFilter;
import com.infy.scm360.common.security.JwtTokenValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@Import({
    CorrelationIdFilter.class,
    CorrelationFeignInterceptor.class,
    GlobalExceptionHandler.class,
    CommonSecurityConfig.class,
    JwtTokenValidator.class,
    JwtAuthenticationFilter.class,
    RabbitMqConfig.class,
    OutboxService.class,
    OutboxRelay.class,
    IdempotentConsumer.class
})
@EntityScan(basePackages = {
    "com.infy.scm360"
})
@EnableJpaRepositories(basePackages = {
    "com.infy.scm360"
})
public class ScmCommonAutoConfiguration {
}
