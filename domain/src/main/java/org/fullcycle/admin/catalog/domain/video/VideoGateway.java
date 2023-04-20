package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.pagination.Pagination;

public interface VideoGateway {

    Video create(final Video video);
    Video update(final Video video);
    void deleteById(final VideoID id);
    Video findById(final VideoID id);
    Pagination<Video> findAll(final VideoSearchQuery query);

}
