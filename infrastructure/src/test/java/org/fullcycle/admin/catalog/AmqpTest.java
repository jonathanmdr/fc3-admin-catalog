package org.fullcycle.admin.catalog;

import org.fullcycle.admin.catalog.infrastructure.configuration.WebServerConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest(classes = WebServerConfiguration.class)
@ActiveProfiles("integration-test")
public @interface AmqpTest {

}
