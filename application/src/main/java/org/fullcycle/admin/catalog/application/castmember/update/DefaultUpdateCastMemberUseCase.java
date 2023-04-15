package org.fullcycle.admin.catalog.application.castmember.update;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(final UpdateCastMemberCommand command) {
        final var id = CastMemberID.from(command.id());
        final var name = command.name();
        final var type = command.type();

        final var castMember = this.castMemberGateway.findById(id)
                .orElseThrow(castMemberNotFound(id));

        final var notification = NotificationHandler.create();

        castMember.update(name, type)
            .validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to update the aggregate CastMember", notification);
        }

        return UpdateCastMemberOutput.from(
            this.castMemberGateway.update(castMember)
        );
    }

    private Supplier<NotFoundException> castMemberNotFound(final CastMemberID id) {
        return () -> NotFoundException.with(CastMember.class, id);
    }

}
