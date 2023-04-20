package org.fullcycle.admin.catalog.domain.video;

public record VideoSearchQuery(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {

}
