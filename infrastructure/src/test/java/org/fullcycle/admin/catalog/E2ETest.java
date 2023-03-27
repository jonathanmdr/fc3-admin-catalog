package org.fullcycle.admin.catalog;

import org.fullcycle.admin.catalog.infrastructure.configuration.WebServerConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional
@AutoConfigureTestEntityManager
@SpringBootTest(classes = WebServerConfiguration.class)
@ActiveProfiles("e2e-test")
@ExtendWith(CleanupDatabaseExtension.class)
@AutoConfigureMockMvc
public @interface E2ETest {

}
