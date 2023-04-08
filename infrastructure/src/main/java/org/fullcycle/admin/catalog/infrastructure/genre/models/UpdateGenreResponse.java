package org.fullcycle.admin.catalog.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateGenreResponse(
    @JsonProperty("id") String id
) {
}
