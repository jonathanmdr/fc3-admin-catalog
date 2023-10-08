package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.resource.Resource;

import java.util.Optional;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(final VideoID videoId, final VideoResource videoResource);
    ImageMedia storeImage(final VideoID videoId, final VideoResource videoResource);

    Optional<Resource> getResource(final VideoID videoID, final MediaType mediaType);

    void clearResources(final VideoID videoId);

}
