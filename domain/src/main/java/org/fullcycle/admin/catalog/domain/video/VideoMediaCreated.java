package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.events.DomainEvent;

import java.time.Instant;

public record VideoMediaCreated(
    String resourceId,
    String filePath,
    Instant occurredOn
) implements DomainEvent {

    public static VideoMediaCreated with(final String resourceId, final String filePath) {
        return new VideoMediaCreated(resourceId, filePath, Instant.now());
    }

}
