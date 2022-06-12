package org.fullcycle.admin.catalog.infrastructure;

import org.fullcycle.admin.catalog.infrastructure.configuration.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class Main {

    private static final String SPRING_DEFAULT_PROFILE = "dev";

    public static void main(String ... args) {
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, SPRING_DEFAULT_PROFILE);
        SpringApplication.run(WebServerConfig.class, args);
    }

}
