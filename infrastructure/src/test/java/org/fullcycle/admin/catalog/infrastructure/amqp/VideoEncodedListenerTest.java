package org.fullcycle.admin.catalog.infrastructure.amqp;

import org.fullcycle.admin.catalog.AmqpTest;
import org.fullcycle.admin.catalog.application.video.media.update.UpdateMediaStatusCommand;
import org.fullcycle.admin.catalog.application.video.media.update.UpdateMediaStatusUseCase;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.infrastructure.configuration.annotations.VideoEncodedQueueProperties;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.amqp.QueueProperties;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoEncoderCompleted;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoEncoderError;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoMessage;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@AmqpTest
class VideoEncodedListenerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    @VideoEncodedQueueProperties
    private QueueProperties queueProperties;

    @MockBean
    private UpdateMediaStatusUseCase updateMediaStatusUseCase;

    @Test
    void givenErrorResult_whenCallsListener_shouldProcess() throws InterruptedException {
        final var videoEncoderErrorMessage = new VideoMessage("123", "video.mp4");
        final var expectedVideoEncoderError = new VideoEncoderError(videoEncoderErrorMessage, "Video not found");
        final var expectedEventMessage = Json.writeValueAsString(expectedVideoEncoderError);

        this.rabbitTemplate.convertAndSend(this.queueProperties.getQueue(), expectedEventMessage);

        final var actual = this.harness.getNextInvocationDataFor(VideoEncodedListener.VIDEO_ENCODED_LISTENER_ID, 1, TimeUnit.SECONDS);

        assertThat(actual)
            .isNotNull()
            .extracting(invocationData -> invocationData.getArguments()[0].toString())
            .isEqualTo(expectedEventMessage);
    }

    @Test
    void givenCompletedResult_whenCallsListener_shouldCallsUseCase() throws InterruptedException {
        final var expectedId = IdentifierUtils.unique();
        final var expectedMediaStatus = MediaStatus.COMPLETED;
        final var expectedOutputBucket = "output-bucket-path";
        final var expectedEncoderVideoFolder = "encoder-video-folder";
        final var expectedResourceId = IdentifierUtils.unique();
        final var expectedFilePath = "file-path";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);
        final var videoEncoderCompleted = new VideoEncoderCompleted(expectedId, expectedOutputBucket, expectedMetadata);
        final var expectedEventMessage = Json.writeValueAsString(videoEncoderCompleted);

        doNothing()
            .when(this.updateMediaStatusUseCase).execute(any(UpdateMediaStatusCommand.class));

        this.rabbitTemplate.convertAndSend(this.queueProperties.getQueue(), expectedEventMessage);

        final var actual = this.harness.getNextInvocationDataFor(VideoEncodedListener.VIDEO_ENCODED_LISTENER_ID, 1, TimeUnit.SECONDS);

        assertThat(actual)
            .isNotNull()
            .extracting(invocationData -> invocationData.getArguments()[0].toString())
            .isEqualTo(expectedEventMessage);

        final ArgumentCaptor<UpdateMediaStatusCommand> updateMediaStatusCommandArgumentCaptor = ArgumentCaptor.forClass(UpdateMediaStatusCommand.class);
        verify(this.updateMediaStatusUseCase).execute(updateMediaStatusCommandArgumentCaptor.capture());

        final var actualCommand = updateMediaStatusCommandArgumentCaptor.getValue();
        assertThat(actualCommand)
            .isNotNull()
            .hasFieldOrPropertyWithValue("mediaStatus", expectedMediaStatus)
            .hasFieldOrPropertyWithValue("videoId", expectedId)
            .hasFieldOrPropertyWithValue("resourceId", expectedResourceId)
            .hasFieldOrPropertyWithValue("folder", expectedEncoderVideoFolder)
            .hasFieldOrPropertyWithValue("filename", expectedFilePath);
    }

}
