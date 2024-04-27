package org.fullcycle.admin.catalog.infrastructure.amqp;

import org.fullcycle.admin.catalog.application.video.media.update.UpdateMediaStatusCommand;
import org.fullcycle.admin.catalog.application.video.media.update.UpdateMediaStatusUseCase;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoEncoderCompleted;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoEncoderError;
import org.fullcycle.admin.catalog.infrastructure.video.models.VideoEncoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class VideoEncodedListener {

    private static final Logger log = LoggerFactory.getLogger(VideoEncodedListener.class);
    protected static final String VIDEO_ENCODED_LISTENER_ID = "video-encoded-listener";

    private final UpdateMediaStatusUseCase updateMediaStatusUseCase;

    public VideoEncodedListener(final UpdateMediaStatusUseCase updateMediaStatusUseCase) {
        this.updateMediaStatusUseCase = updateMediaStatusUseCase;
    }

    @RabbitListener(id = VIDEO_ENCODED_LISTENER_ID, queues = "${amqp.queues.video-encoded.queue}")
    public void onVideoEncodedMessage(@Payload final String message) {
        final VideoEncoderResult videoEncoderResult = Json.readValue(message, VideoEncoderResult.class);

        switch (videoEncoderResult) {
            case VideoEncoderCompleted completed -> this.handleCompleted(completed, message);
            case VideoEncoderError error -> this.handleError(error, message);
            default -> log.error("[message: video.listener.income] [status: unknown] [payload: {}]", message);
        }
    }

    private void handleCompleted(final VideoEncoderCompleted videoEncoderCompleted, final String message) {
        final var command = UpdateMediaStatusCommand.with(
            MediaStatus.COMPLETED,
            videoEncoderCompleted.id(),
            videoEncoderCompleted.videoMetadata().resourceId(),
            videoEncoderCompleted.videoMetadata().encodedVideoFolder(),
            videoEncoderCompleted.videoMetadata().filePath()
        );
        this.updateMediaStatusUseCase.execute(command);
        log.info("[message: video.listener.income] [status: completed] [payload: {}]", message);
    }

    @SuppressWarnings("all")
    private void handleError(final VideoEncoderError videoEncoderError, final String message) {
        log.error("[message: video.listener.income] [status: error] [payload: {}]", message);
    }

}
