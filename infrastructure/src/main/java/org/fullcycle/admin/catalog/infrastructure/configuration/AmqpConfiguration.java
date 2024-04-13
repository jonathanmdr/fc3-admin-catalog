package org.fullcycle.admin.catalog.infrastructure.configuration;

import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoCreatedBinding;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoCreatedQueue;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoCreatedQueueProperties;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoEncodedBinding;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoEncodedQueue;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoEncodedQueueProperties;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoEventsExchange;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.amqp.QueueProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    @Bean
    @VideoCreatedQueueProperties
    @ConfigurationProperties("amqp.queues.video-created")
    public QueueProperties videoCreatedQueueProperties() {
        return new QueueProperties();
    }

    @Bean
    @VideoEncodedQueueProperties
    @ConfigurationProperties("amqp.queues.video-encoded")
    public QueueProperties videoEncodedQueueProperties() {
        return new QueueProperties();
    }

    @Configuration
    static class Admin {

        @Bean
        @VideoEventsExchange
        public Exchange videoEventsExchange(@VideoCreatedQueueProperties final QueueProperties properties) {
            return new DirectExchange(properties.getExchange());
        }

        @Bean
        @VideoCreatedQueue
        public Queue videoCreatedQueue(@VideoCreatedQueueProperties final QueueProperties properties) {
            return new Queue(properties.getQueue(), true);
        }

        @Bean
        @VideoEncodedQueue
        public Queue videoEncodedQueue(@VideoEncodedQueueProperties final QueueProperties properties) {
            return new Queue(properties.getQueue(), true);
        }

        @Bean
        @VideoCreatedBinding
        public Binding videoCreatedBinding(
            @VideoEventsExchange DirectExchange exchange,
            @VideoCreatedQueue Queue queue,
            @VideoCreatedQueueProperties QueueProperties properties
        ) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getRoutingKey());
        }

        @Bean
        @VideoEncodedBinding
        public Binding videoEncodedBinding(
            @VideoEventsExchange DirectExchange exchange,
            @VideoEncodedQueue Queue queue,
            @VideoEncodedQueueProperties QueueProperties properties
        ) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getRoutingKey());
        }

    }

}
