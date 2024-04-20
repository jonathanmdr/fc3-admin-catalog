package org.fullcycle.admin.catalog.infrastructure.services.impl;

import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.fullcycle.admin.catalog.infrastructure.services.EventService;
import org.springframework.amqp.rabbit.core.RabbitOperations;

import java.util.Objects;

public class RabbitEventService implements EventService {

    private final String exchange;
    private final String routingKey;
    private final RabbitOperations rabbitOperations;

    public RabbitEventService(final String exchange, final String routingKey, final RabbitOperations rabbitOperations) {
        this.exchange = Objects.requireNonNull(exchange);
        this.routingKey = Objects.requireNonNull(routingKey);
        this.rabbitOperations = Objects.requireNonNull(rabbitOperations);
    }

    @Override
    public void send(final Object event) {
        this.rabbitOperations.convertAndSend(this.exchange, this.routingKey, Json.writeValueAsString(event));
    }

}
