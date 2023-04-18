package org.fullcycle.admin.catalog.infrastructure.castmember.presenters;

import org.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberOutput;
import org.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdOutput;
import org.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMembersOutput;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.GetCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.ListCastMembersResponse;

public interface CastMemberApiPresenter {

    static ListCastMembersResponse present(final ListCastMembersOutput output) {
        return new ListCastMembersResponse(
            output.id().getValue(),
            output.name(),
            output.type().name(),
            output.createdAt()
        );
    }

    static GetCastMemberResponse present(final GetCastMemberByIdOutput output) {
        return new GetCastMemberResponse(
            output.id().getValue(),
            output.name(),
            output.type().name(),
            output.createdAt(),
            output.updatedAt()
        );
    }

    static CreateCastMemberResponse present(final CreateCastMemberOutput output) {
        return new CreateCastMemberResponse(output.id());
    }

}
