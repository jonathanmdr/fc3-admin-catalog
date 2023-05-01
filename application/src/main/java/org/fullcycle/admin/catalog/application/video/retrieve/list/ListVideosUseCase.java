package org.fullcycle.admin.catalog.application.video.retrieve.list;

import org.fullcycle.admin.catalog.application.UseCase;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;

public abstract class ListVideosUseCase extends UseCase<ListVideosCommand, Pagination<ListVideosOutput>> {
}
