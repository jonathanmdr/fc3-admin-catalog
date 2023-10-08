package org.fullcycle.admin.catalog.domain.video;

import java.util.Arrays;
import java.util.Optional;

public enum MediaType {

    VIDEO,
    TRAILER,
    BANNER,
    THUMBNAIL,
    THUMBNAIL_HALF;

    public static Optional<MediaType> of(final String value) {
        return Arrays.stream(values())
            .filter(mediaType -> mediaType.name().equalsIgnoreCase(value))
            .findFirst();
    }

}
