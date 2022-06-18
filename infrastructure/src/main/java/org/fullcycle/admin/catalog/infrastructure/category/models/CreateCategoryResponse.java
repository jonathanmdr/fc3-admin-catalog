package org.fullcycle.admin.catalog.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCategoryResponse(
    @JsonProperty("id") String id
) {

}
