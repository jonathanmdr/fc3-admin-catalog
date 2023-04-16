package org.fullcycle.admin.catalog.application.castmember.delete;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;

import java.util.Objects;

public class DefaultDeleteCastMemberUseCase extends DeleteCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultDeleteCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public void execute(final DeleteCastMemberCommand command) {
        final var id = CastMemberID.from(command.id());
        this.castMemberGateway.deleteById(id);
    }

}
