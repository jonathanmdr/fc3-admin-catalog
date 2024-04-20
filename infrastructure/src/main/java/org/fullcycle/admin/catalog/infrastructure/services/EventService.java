package org.fullcycle.admin.catalog.infrastructure.services;

@FunctionalInterface
public interface EventService {

    void send(Object event);

}
