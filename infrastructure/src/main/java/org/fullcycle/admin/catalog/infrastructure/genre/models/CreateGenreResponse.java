package org.fullcycle.admin.catalog.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateGenreResponse(
    @JsonProperty("id") String id
) { }
