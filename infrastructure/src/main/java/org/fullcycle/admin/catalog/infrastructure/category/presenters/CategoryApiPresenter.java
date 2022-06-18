package org.fullcycle.admin.catalog.infrastructure.category.presenters;

import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdOutput;
import org.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoryOutput;
import org.fullcycle.admin.catalog.infrastructure.category.models.GetCategoryResponse;
import org.fullcycle.admin.catalog.infrastructure.category.models.ListCategoryResponse;

public interface CategoryApiPresenter {

    static GetCategoryResponse present(final GetCategoryByIdOutput output) {
        return new GetCategoryResponse(
            output.id().getValue(),
            output.name(),
            output.description(),
            output.isActive(),
            output.createdAt(),
            output.updatedAt(),
            output.deletedAt()
        );
    }

    static ListCategoryResponse present(final ListCategoryOutput output) {
        return new ListCategoryResponse(
            output.id().getValue(),
            output.name(),
            output.description(),
            output.isActive(),
            output.createdAt(),
            output.deletedAt()
        );
    }

}
