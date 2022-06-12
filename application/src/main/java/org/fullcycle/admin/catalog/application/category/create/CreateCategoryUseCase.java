package org.fullcycle.admin.catalog.application.category.create;

import io.vavr.control.Either;
import org.fullcycle.admin.catalog.application.UseCase;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationValidationHandler;

public abstract class CreateCategoryUseCase extends UseCase<CreateCategoryCommand, Either<NotificationValidationHandler, CreateCategoryOutput>> {

}
