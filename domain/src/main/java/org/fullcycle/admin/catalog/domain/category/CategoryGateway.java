package org.fullcycle.admin.catalog.domain.category;

import org.fullcycle.admin.catalog.domain.pagination.Pagination;

import java.util.Optional;

public interface CategoryGateway {

    Pagination<Category> findAll(final CategorySearchQuery categorySearchQuery);
    Optional<Category> findById(final CategoryID categoryID);
    Category create(final Category category);
    Category update(final Category category);
    void deleteById(final CategoryID categoryID);

}
