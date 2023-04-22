package org.fullcycle.admin.catalog.domain.castmember;

import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface CastMemberGateway {

    Pagination<CastMember> findAll(final SearchQuery searchQuery);
    Optional<CastMember> findById(final CastMemberID castMemberID);
    CastMember create(final CastMember castMember);
    CastMember update(final CastMember castMember);
    void deleteById(final CastMemberID castMemberID);
    List<CastMemberID> existsByIds(final Iterable<CastMemberID> categoryIds);

}
