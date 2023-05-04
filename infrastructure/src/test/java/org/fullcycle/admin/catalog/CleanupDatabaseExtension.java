package org.fullcycle.admin.catalog;

import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.fullcycle.admin.catalog.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class CleanupDatabaseExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final var applicationContext = SpringExtension.getApplicationContext(context);

        cleanup(
            List.of(
                applicationContext.getBean(VideoRepository.class),
                applicationContext.getBean(GenreRepository.class),
                applicationContext.getBean(CategoryRepository.class),
                applicationContext.getBean(CastMemberRepository.class)
            )
        );
    }

    private void cleanup(final Collection<CrudRepository<?, ?>> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}