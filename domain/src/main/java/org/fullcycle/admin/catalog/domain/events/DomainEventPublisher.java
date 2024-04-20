package org.fullcycle.admin.catalog.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {

    void publish(final DomainEvent event);

}
