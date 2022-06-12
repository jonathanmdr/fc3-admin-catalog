package org.fullcycle.admin.catalog.application.category.update;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;

public record UpdateCategoryOutput(
    CategoryID id
) {

    public static UpdateCategoryOutput from(final Category category) {
        return new UpdateCategoryOutput(category.getId());
    }

}
