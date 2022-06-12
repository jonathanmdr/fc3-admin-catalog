package org.fullcycle.admin.catalog.application.category.update;

import io.vavr.control.Either;
import org.fullcycle.admin.catalog.application.UseCase;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationValidationHandler;

public abstract class UpdateCategoryUseCase extends UseCase<UpdateCategoryCommand, Either<NotificationValidationHandler, UpdateCategoryOutput>> {

}
