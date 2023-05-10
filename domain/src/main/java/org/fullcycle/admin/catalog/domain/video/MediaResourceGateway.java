package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.resource.Resource;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(final VideoID videoId, final VideoResource videoResource);
    ImageMedia storeImage(final VideoID videoId, final VideoResource videoResource);
    void clearResources(final VideoID videoId);

}
