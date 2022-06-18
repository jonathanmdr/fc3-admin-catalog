package org.fullcycle.admin.catalog.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return Json.mapper();
    }

}
