package org.fullcycle.admin.catalog.application.video.media.update;

import org.fullcycle.admin.catalog.domain.video.MediaStatus;

public record UpdateMediaStatusCommand(
    MediaStatus mediaStatus,
    String videoId,
    String resourceId,
    String folder,
    String filename
) {

    public static UpdateMediaStatusCommand with(
        final MediaStatus mediaStatus,
        final String videoId,
        final String resourceId,
        final String folder,
        final String filename
    ) {
        return new UpdateMediaStatusCommand(mediaStatus, videoId, resourceId, folder, filename);
    }

}
