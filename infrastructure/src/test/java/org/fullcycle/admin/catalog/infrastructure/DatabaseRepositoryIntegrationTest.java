package org.fullcycle.admin.catalog.infrastructure;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(DatabaseRepositoryIntegrationTest.CleanupExtension.class)
public @interface DatabaseRepositoryIntegrationTest {

    class CleanupExtension implements BeforeEachCallback {

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

}
