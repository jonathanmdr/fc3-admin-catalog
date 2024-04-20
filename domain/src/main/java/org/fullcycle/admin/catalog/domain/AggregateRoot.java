package org.fullcycle.admin.catalog.domain;

import org.fullcycle.admin.catalog.domain.events.DomainEvent;

import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<T extends Identifier> extends Entity<T> {

    protected AggregateRoot(final T id) {
        super(id, Collections.emptyList());
    }

    protected AggregateRoot(final T id, final List<DomainEvent> domainEvents) {
        super(id, domainEvents);
    }

}
