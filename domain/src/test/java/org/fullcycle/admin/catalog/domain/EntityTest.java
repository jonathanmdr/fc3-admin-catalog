package org.fullcycle.admin.catalog.domain;

import org.fullcycle.admin.catalog.domain.events.DomainEvent;
import org.fullcycle.admin.catalog.domain.events.DomainEventPublisher;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityTest {

    @Test
    void givenNullableDomainEvents_whenInstantiate_thenShouldBeOk() {
        final var entity = new DummyEntity(new DummyID(), null);
        assertNotNull(entity.getDomainEvents());
        assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    void givenDomainEvents_whenInstantiate_thenShouldBeApplyDefensiveClone() {
        final List<DomainEvent> domainEvents = new ArrayList<>();
        domainEvents.add(Instant::now);

        final var entity = new DummyEntity(new DummyID(), domainEvents);
        assertNotNull(entity.getDomainEvents());
        assertEquals(1, entity.getDomainEvents().size());

        assertThrows(
            RuntimeException.class,
            () -> entity.getDomainEvents().removeFirst()
        );
    }

    @Test
    void givenEmptyDomainEvents_whenCallsRegisterEvent_thenAddEventToList() {
        final List<DomainEvent> domainEvents = new ArrayList<>();

        final var entity = new DummyEntity(new DummyID(), domainEvents);
        assertNotNull(entity.getDomainEvents());
        assertTrue(entity.getDomainEvents().isEmpty());

        entity.registerEvent(Instant::now);

        assertEquals(1, entity.getDomainEvents().size());
    }

    @Test
    void givenDomainEvents_whenCallsPublishDomainEvents_thenPublishAndClearList() {
        final var counter = new AtomicInteger(0);
        final List<DomainEvent> domainEvents = new ArrayList<>();
        domainEvents.add(Instant::now);
        domainEvents.add(Instant::now);
        domainEvents.add(Instant::now);

        final var entity = new DummyEntity(new DummyID(), domainEvents);
        assertNotNull(entity.getDomainEvents());
        assertEquals(3, entity.getDomainEvents().size());

        entity.publishDomainEvents(event -> counter.getAndIncrement());

        assertEquals(0, entity.getDomainEvents().size());
        assertEquals(3, counter.get());
    }

    private static class DummyID extends Identifier {

        private final String id;

        public DummyID() {
            this.id = IdentifierUtils.unique();
        }

        @Override
        public String getValue() {
            return this.id;
        }

    }

    private static class DummyEntity extends Entity<DummyID> {

        private DummyEntity(final DummyID dummyID, final List<DomainEvent> domainEvents) {
            super(dummyID, domainEvents);
        }

        @Override
        public void validate(final ValidationHandler handler) {

        }

    }

}
