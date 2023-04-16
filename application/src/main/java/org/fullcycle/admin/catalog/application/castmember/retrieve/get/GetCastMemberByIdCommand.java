package org.fullcycle.admin.catalog.application.castmember.retrieve.get;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;

public record GetCastMemberByIdCommand(
    String id
) {

    public static GetCastMemberByIdCommand with(final String id) {
        return new GetCastMemberByIdCommand(
            CastMemberID.from(id).getValue()
        );
    }

}
