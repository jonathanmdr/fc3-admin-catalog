package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.pagination.Pagination;

import java.util.Optional;

public interface VideoGateway {

    Video create(final Video video);
    Video update(final Video video);
    void deleteById(final VideoID id);
    Optional<Video> findById(final VideoID id);
    Pagination<VideoPreview> findAll(final VideoSearchQuery query);

}
