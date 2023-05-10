package org.fullcycle.admin.catalog.infrastructure.services.local;

import org.fullcycle.admin.catalog.domain.utils.CollectionUtils;
import org.fullcycle.admin.catalog.domain.resource.Resource;
import org.fullcycle.admin.catalog.infrastructure.services.StorageService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageService implements StorageService {

    private final Map<String, Resource> storage;

    public InMemoryStorageService() {
        this.storage = new ConcurrentHashMap<>();
    }

    public Map<String, Resource> storage() {
        return this.storage;
    }

    public void reset() {
        this.storage.clear();
    }

    @Override
    public Optional<Resource> get(final String fileName) {
        return Objects.isNull(fileName) ? Optional.empty() : Optional.of(this.storage.get(fileName));
    }

    @Override
    public List<String> findAll(final String prefixName) {
        if (Objects.isNull(prefixName)) {
            return Collections.emptyList();
        }

        return this.storage.keySet()
            .stream()
            .filter(it -> it.startsWith(prefixName))
            .toList();
    }

    @Override
    public void store(final String fileName, final Resource resource) {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(resource);

        this.storage.put(fileName, resource);
    }

    @Override
    public void deleteAll(final Collection<String> fileNames) {
        if (CollectionUtils.isNonNullAndNonEmpty(fileNames)) {
            fileNames.forEach(this.storage::remove);
        }
    }

}
