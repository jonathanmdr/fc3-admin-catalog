package org.fullcycle.admin.catalog.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GetGenreResponse(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("categories_ids") List<String> categories,
    @JsonProperty("is_active") Boolean active,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("updated_at") Instant updatedAt,
    @JsonProperty("deleted_at") Instant deletedAt
) { }
