package org.fullcycle.admin.catalog.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCategoryResponse(
    @JsonProperty("id") String id
) {

}
