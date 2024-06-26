package org.fullcycle.admin.catalog.domain;

import org.fullcycle.admin.catalog.domain.events.DomainEvent;
import org.fullcycle.admin.catalog.domain.events.DomainEventPublisher;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Entity<T extends Identifier> {

    protected final T id;
    private final List<DomainEvent> domainEvents;

    protected Entity(final T id, final List<DomainEvent> domainEvents) {
        Objects.requireNonNull(id, "'id' should not be null");
        this.id = id;
        this.domainEvents = new ArrayList<>(domainEvents == null ? Collections.emptyList() : domainEvents);
    }

    public abstract void validate(final ValidationHandler handler);

    public T getId() {
        return id;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void publishDomainEvents(final DomainEventPublisher domainEventPublisher) {
        if (domainEventPublisher != null) {
            getDomainEvents().forEach(domainEventPublisher::publish);
            this.domainEvents.clear();
        }
    }

    public void registerEvent(final DomainEvent domainEvent) {
        if (domainEvent != null) {
            this.domainEvents.add(domainEvent);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
