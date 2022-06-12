package org.fullcycle.admin.catalog.application.category.delete;

import org.fullcycle.admin.catalog.domain.category.CategoryID;

public record DeleteCategoryCommand(
    String id
) {

    public static DeleteCategoryCommand with(final String id) {
        return new DeleteCategoryCommand(
            CategoryID.from(id).getValue()
        );
    }

}
