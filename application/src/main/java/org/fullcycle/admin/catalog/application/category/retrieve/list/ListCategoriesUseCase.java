package org.fullcycle.admin.catalog.application.category.retrieve.list;

import org.fullcycle.admin.catalog.application.UseCase;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<ListCategoriesCommand, Pagination<ListCategoryOutput>> {

}
