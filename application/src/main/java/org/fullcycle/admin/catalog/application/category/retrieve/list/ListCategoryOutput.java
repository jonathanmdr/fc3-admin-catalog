package org.fullcycle.admin.catalog.application.category.retrieve.list;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;

import java.time.Instant;

public record ListCategoryOutput(
    CategoryID id,
    String name,
    String description,
    boolean isActive,
    Instant createdAt,
    Instant deletedAt
) {

    public static ListCategoryOutput from(final Category category) {
        return new ListCategoryOutput(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.isActive(),
            category.getCreatedAt(),
            category.getDeletedAt()
        );
    }

}
