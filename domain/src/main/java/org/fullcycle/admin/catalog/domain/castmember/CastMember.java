package org.fullcycle.admin.catalog.domain.castmember;

import org.fullcycle.admin.catalog.domain.AggregateRoot;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.time.Instant;
import java.util.Objects;

public class CastMember extends AggregateRoot<CastMemberID> {

    private String name;
    private CastMemberType type;
    private final Instant createdAt;
    private Instant updatedAt;

    private CastMember(
        final CastMemberID id,
        final String name,
        final CastMemberType type,
        final Instant createdAt,
        final Instant updatedAt
    ) {
        super(id);
        this.name = name;
        this.type = type;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' should not be null");
        selfValidate();
    }

    public static CastMember newMember(final String name, final CastMemberType type) {
        final var id = CastMemberID.unique();
        final var now = Instant.now();
        return new CastMember(id, name, type, now, now);
    }

    public static CastMember with(final CastMember castMember) {
        return with(
            castMember.getId(),
            castMember.getName(),
            castMember.getType(),
            castMember.getCreatedAt(),
            castMember.getUpdatedAt()
        );
    }

    public static CastMember with(
        final CastMemberID id,
        final String name,
        final CastMemberType type,
        final Instant createdAt,
        final Instant updatedAt
    ) {
        return new CastMember(id, name, type, createdAt, updatedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CastMemberValidator(this, handler).validate();
    }

    public CastMember update(final String name, final CastMemberType type) {
        this.name = name;
        this.type = type;
        this.updatedAt = Instant.now();
        selfValidate();
        return this;
    }

    public String getName() {
        return name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private void selfValidate() {
        final var notification = NotificationHandler.create();
        validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to create a aggregate CastMember", notification);
        }
    }

}