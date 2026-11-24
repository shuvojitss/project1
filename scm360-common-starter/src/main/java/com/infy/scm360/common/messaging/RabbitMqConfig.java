package com.infy.scm360.common.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infy.scm360.common.event.EventTypes;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
public class RabbitMqConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        ObjectMapper mapper = objectMapper.copy();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setMandatory(true);
        return template;
    }

    // Dead letter exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EventTypes.EXCHANGE_DLX, true, false);
    }

    // Domain Exchanges
    @Bean
    public TopicExchange catalogExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_CATALOG, true, false);
    }

    @Bean
    public TopicExchange supplierExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_SUPPLIER, true, false);
    }

    @Bean
    public TopicExchange procurementExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_PROCUREMENT, true, false);
    }

    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_INVENTORY, true, false);
    }

    @Bean
    public TopicExchange warehouseExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_WAREHOUSE, true, false);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_ORDER, true, false);
    }

    @Bean
    public TopicExchange transportExchange() {
        return new TopicExchange(EventTypes.EXCHANGE_TRANSPORT, true, false);
    }
}
