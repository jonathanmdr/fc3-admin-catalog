package org.fullcycle.admin.catalog.application.castmember.retrieve.list;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;

import java.time.Instant;

public record ListCastMembersOutput(
    CastMemberID id,
    String name,
    CastMemberType type,
    Instant createdAt
) {

    public static ListCastMembersOutput from(final CastMember castMember) {
        return new ListCastMembersOutput(
            castMember.getId(),
            castMember.getName(),
            castMember.getType(),
            castMember.getCreatedAt()
        );
    }

}
