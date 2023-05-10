package org.fullcycle.admin.catalog.infrastructure.services;

import org.fullcycle.admin.catalog.domain.resource.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StorageService {

    Optional<Resource> get(final String fileName);
    List<String> findAll(final String prefixName);
    void store(final String fileName, final Resource resource);
    void deleteAll(final Collection<String> fileNames);

}
