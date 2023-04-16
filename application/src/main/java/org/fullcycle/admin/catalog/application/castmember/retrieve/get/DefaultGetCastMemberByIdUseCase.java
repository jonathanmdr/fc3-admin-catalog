package org.fullcycle.admin.catalog.application.castmember.retrieve.get;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCastMemberByIdUseCase extends GetCastMemberByIdUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultGetCastMemberByIdUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public GetCastMemberByIdOutput execute(final GetCastMemberByIdCommand command) {
        final var id = CastMemberID.from(command.id());

        return this.castMemberGateway.findById(id)
            .map(GetCastMemberByIdOutput::from)
            .orElseThrow(castMemberNotFound(id));
    }

    private Supplier<NotFoundException> castMemberNotFound(final CastMemberID id) {
        return () -> NotFoundException.with(CastMember.class, id);
    }

}
