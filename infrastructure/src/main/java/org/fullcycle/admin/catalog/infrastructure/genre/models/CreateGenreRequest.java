package org.fullcycle.admin.catalog.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateGenreRequest(
    @JsonProperty("name") String name,
    @JsonProperty("categories_ids") List<String> categories,
    @JsonProperty("is_active") Boolean active
) { }
