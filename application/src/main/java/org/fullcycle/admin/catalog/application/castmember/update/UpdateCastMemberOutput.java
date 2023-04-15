package org.fullcycle.admin.catalog.application.castmember.update;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;

public record UpdateCastMemberOutput(
    String id
) {

    public static UpdateCastMemberOutput from(final String castMemberId) {
        return new UpdateCastMemberOutput(castMemberId);
    }

    public static UpdateCastMemberOutput from(final CastMember castMember) {
        return new UpdateCastMemberOutput(castMember.getId().getValue());
    }

}
