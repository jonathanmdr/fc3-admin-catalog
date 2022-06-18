package org.fullcycle.admin.catalog.application.category.create;

import org.fullcycle.admin.catalog.domain.category.Category;

public record CreateCategoryOutput(
    String id
) {

    public static CreateCategoryOutput from(final String categoryId) {
        return new CreateCategoryOutput(categoryId);
    }

    public static CreateCategoryOutput from(final Category category) {
        return new CreateCategoryOutput(category.getId().getValue());
    }

}
