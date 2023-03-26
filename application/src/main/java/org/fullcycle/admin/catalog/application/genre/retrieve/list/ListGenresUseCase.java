package org.fullcycle.admin.catalog.application.genre.retrieve.list;

import org.fullcycle.admin.catalog.application.UseCase;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;

public abstract class ListGenresUseCase extends UseCase<ListGenresCommand, Pagination<ListGenresOutput>> {
}
