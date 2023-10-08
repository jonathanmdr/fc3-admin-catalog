package org.fullcycle.admin.catalog.application.video.media.get;

import org.fullcycle.admin.catalog.domain.resource.Resource;

public record GetMediaOutput(
    byte[] content,
    String contentType,
    String name
) {

    public static GetMediaOutput with(
        final Resource resource
    ) {
        return new GetMediaOutput(
            resource.content(),
            resource.contentType(),
            resource.name()
        );
    }

}
