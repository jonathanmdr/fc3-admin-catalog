package org.fullcycle.admin.catalog;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(includeFilters = {
    @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".[DatabaseGateway]"
    )
})
@ExtendWith(CleanupDatabaseExtension.class)
public @interface DatabaseGatewayIntegrationTest {

}
