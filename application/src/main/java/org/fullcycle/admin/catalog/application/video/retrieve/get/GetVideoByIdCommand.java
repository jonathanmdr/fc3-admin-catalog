package org.fullcycle.admin.catalog.application.video.retrieve.get;

import org.fullcycle.admin.catalog.domain.video.VideoID;

public record GetVideoByIdCommand(
    String id
) {

    public static GetVideoByIdCommand with(final String id) {
        return new GetVideoByIdCommand(
            VideoID.from(id).getValue()
        );
    }

}
