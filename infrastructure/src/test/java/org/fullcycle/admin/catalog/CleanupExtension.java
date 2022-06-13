package org.fullcycle.admin.catalog;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

public class CleanupExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final var repositories = SpringExtension.getApplicationContext(context)
            .getBeansOfType(CrudRepository.class)
            .values();

        cleanup(repositories);
    }

    private void cleanup(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}