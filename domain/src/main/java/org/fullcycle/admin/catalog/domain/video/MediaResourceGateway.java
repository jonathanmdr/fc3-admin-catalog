package org.fullcycle.admin.catalog.domain.video;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(final VideoID videoId, final Resource resource);
    ImageMedia storeImage(final VideoID videoId, final Resource resource);
    void clearResources(final VideoID videoId);

}
