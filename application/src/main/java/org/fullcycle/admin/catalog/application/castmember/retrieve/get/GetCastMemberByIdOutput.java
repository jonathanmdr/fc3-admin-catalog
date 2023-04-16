package org.fullcycle.admin.catalog.application.castmember.retrieve.get;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;

import java.time.Instant;

public record GetCastMemberByIdOutput(
    CastMemberID id,
    String name,
    CastMemberType type,
    Instant createdAt,
    Instant updatedAt
) {

    public static GetCastMemberByIdOutput from(final CastMember castMember) {
        return new GetCastMemberByIdOutput(
            castMember.getId(),
            castMember.getName(),
            castMember.getType(),
            castMember.getCreatedAt(),
            castMember.getUpdatedAt()
        );
    }

}
