package org.fullcycle.admin.catalog.infrastructure.video;

import org.fullcycle.admin.catalog.domain.resource.Resource;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoResource;
import org.fullcycle.admin.catalog.infrastructure.configuration.properties.storage.StorageProperties;
import org.fullcycle.admin.catalog.infrastructure.services.StorageService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    private final String fileNamePattern;
    private final String locationPattern;
    private final StorageService storageService;

    public DefaultMediaResourceGateway(
        final StorageProperties storageProperties,
        final StorageService storageService
    ) {
        this.fileNamePattern = Objects.requireNonNull(storageProperties.getFileNamePattern());
        this.locationPattern = Objects.requireNonNull(storageProperties.getLocationPattern());
        this.storageService = Objects.requireNonNull(storageService);
    }

    @Override
    public AudioVideoMedia storeAudioVideo(final VideoID videoId, final VideoResource videoResource) {
        final var filePath = filePath(videoId, videoResource.type());
        final var resource = videoResource.resource();

        this.storageService.store(filePath, resource);

        return AudioVideoMedia.newAudioVideoMedia(
            resource.name(),
            resource.checksum(),
            filePath
        );
    }

    @Override
    public ImageMedia storeImage(final VideoID videoId, final VideoResource videoResource) {
        final var filePath = filePath(videoId, videoResource.type());
        final var resource = videoResource.resource();

        this.storageService.store(filePath, resource);

        return ImageMedia.newImageMedia(
            resource.name(),
            resource.checksum(),
            filePath
        );
    }

    @Override
    public Optional<Resource> getResource(final VideoID videoID, final MediaType mediaType) {
        return this.storageService.get(filePath(videoID, mediaType));
    }

    @Override
    public void clearResources(final VideoID videoId) {
        final var ids = this.storageService.findAll(folder(videoId));
        this.storageService.deleteAll(ids);
    }

    private String fileName(final MediaType mediaType) {
        return fileNamePattern.replace("{type}", mediaType.name());
    }

    private String folder(final VideoID videoID) {
        return locationPattern.replace("{videoId}", videoID.getValue());
    }

    private String filePath(final VideoID videoID, final MediaType mediaType) {
        return folder(videoID)
            .concat("/")
            .concat(fileName(mediaType));
    }

}
