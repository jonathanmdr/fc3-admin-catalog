package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;

import java.util.Set;

public record VideoSearchQuery(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction,
    Set<CategoryID> categories,
    Set<GenreID> genres,
    Set<CastMemberID> castMembers
) {

}
