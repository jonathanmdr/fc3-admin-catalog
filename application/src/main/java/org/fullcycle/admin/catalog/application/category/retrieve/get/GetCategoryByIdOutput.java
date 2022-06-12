package org.fullcycle.admin.catalog.application.category.retrieve.get;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;

import java.time.Instant;

public record GetCategoryByIdOutput(
    CategoryID id,
    String name,
    String description,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {

    public static GetCategoryByIdOutput from(final Category category) {
        return new GetCategoryByIdOutput(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.isActive(),
            category.getCreatedAt(),
            category.getUpdatedAt(),
            category.getDeletedAt()
        );
    }

}
