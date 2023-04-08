package org.fullcycle.admin.catalog.application.castmember.create;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;

public record CreateCastMemberOutput(
    String id
) {

    public static CreateCastMemberOutput from(final String castMemberId) {
        return new CreateCastMemberOutput(castMemberId);
    }

    public static CreateCastMemberOutput from(final CastMember castMember) {
        return new CreateCastMemberOutput(castMember.getId().getValue());
    }

}
