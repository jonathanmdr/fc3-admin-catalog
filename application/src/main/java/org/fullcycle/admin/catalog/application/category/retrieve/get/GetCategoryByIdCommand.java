package org.fullcycle.admin.catalog.application.category.retrieve.get;

import org.fullcycle.admin.catalog.domain.category.CategoryID;

public record GetCategoryByIdCommand(
    String id
) {

    public static GetCategoryByIdCommand with(final String id) {
        return new GetCategoryByIdCommand(
            CategoryID.from(id).getValue()
        );
    }

}
