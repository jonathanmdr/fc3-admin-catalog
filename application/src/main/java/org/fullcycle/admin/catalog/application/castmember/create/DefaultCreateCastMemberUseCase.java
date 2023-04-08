package org.fullcycle.admin.catalog.application.castmember.create;

import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.util.Objects;

public class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(final CreateCastMemberCommand command) {
        final var name = command.name();
        final var type = command.type();

        final var notification = NotificationHandler.create();

        final var castMember = notification.validate(
            () -> CastMember.newMember(name, type)
        );

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to create a aggregate CastMember", notification);
        }

        return CreateCastMemberOutput.from(
            this.castMemberGateway.create(castMember)
        );
    }

}
