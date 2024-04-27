package org.fullcycle.admin.catalog.infrastructure.video.models;

public record VideoMetadata(
    String encodedVideoFolder,
    String resourceId,
    String filePath
) {

}
