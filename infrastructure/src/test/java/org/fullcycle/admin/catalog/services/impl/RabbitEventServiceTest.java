package org.fullcycle.admin.catalog.services.impl;

import org.fullcycle.admin.catalog.AmqpTest;
import org.fullcycle.admin.catalog.domain.video.VideoMediaCreated;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoCreatedEventService;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.fullcycle.admin.catalog.infrastructure.services.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@AmqpTest
class RabbitEventServiceTest {

    private static final String VIDEO_CREATED_LISTENER = "video.created";

    @Autowired
    @VideoCreatedEventService
    private EventService subject;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Test
    void shouldSendMessage() throws InterruptedException {
        final var notification = VideoMediaCreated.with("id", "file");

        this.subject.send(notification);

        final var actual = this.harness.getNextInvocationDataFor(VIDEO_CREATED_LISTENER, 3, TimeUnit.SECONDS);

        assertThat(actual)
            .isNotNull()
            .extracting(invocationData -> Json.readValue(invocationData.getArguments()[0].toString(), VideoMediaCreated.class))
            .isEqualTo(notification);
    }

    @Component
    static class VideoCreatedNewsListener {

        @RabbitListener(id = VIDEO_CREATED_LISTENER, queues = "${amqp.queues.video-created.routing-key}")
        public void onVideoCreated(@Payload String message) {
            System.out.println("Message received: " + message);
        }

    }

}
