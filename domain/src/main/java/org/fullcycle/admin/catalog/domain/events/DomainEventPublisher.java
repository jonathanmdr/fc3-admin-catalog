package org.fullcycle.admin.catalog.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(final T event);

}
