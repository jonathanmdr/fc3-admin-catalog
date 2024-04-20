package org.fullcycle.admin.catalog.infrastructure.configuration;

import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoCreatedEventService;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoCreatedQueueProperties;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.amqp.QueueProperties;
import org.fullcycle.admin.catalog.infrastructure.services.EventService;
import org.fullcycle.admin.catalog.infrastructure.services.impl.RabbitEventService;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    @VideoCreatedEventService
    public EventService videoCreatedEventService(
        @VideoCreatedQueueProperties final QueueProperties queueProperties,
        final RabbitOperations rabbitOperations
    ) {
        return new RabbitEventService(queueProperties.getExchange(), queueProperties.getRoutingKey(), rabbitOperations);
    }

}
