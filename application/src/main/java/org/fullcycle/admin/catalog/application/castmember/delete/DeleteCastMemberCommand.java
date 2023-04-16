package org.fullcycle.admin.catalog.application.castmember.delete;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;

public record DeleteCastMemberCommand(
    String id
) {

    public static DeleteCastMemberCommand with(final String id) {
        return new DeleteCastMemberCommand(
            CastMemberID.from(id).getValue()
        );
    }

}
