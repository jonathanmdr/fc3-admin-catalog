package org.fullcycle.admin.catalog.application.category.update;

import org.fullcycle.admin.catalog.domain.category.Category;

public record UpdateCategoryOutput(
    String id
) {

    public static UpdateCategoryOutput from(final String id) {
        return new UpdateCategoryOutput(id);
    }

    public static UpdateCategoryOutput from(final Category category) {
        return new UpdateCategoryOutput(category.getId().getValue());
    }

}
