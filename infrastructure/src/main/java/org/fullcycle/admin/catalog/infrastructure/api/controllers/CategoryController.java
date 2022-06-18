package org.fullcycle.admin.catalog.infrastructure.api.controllers;

import org.fullcycle.admin.catalog.application.category.create.CreateCategoryCommand;
import org.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.delete.DeleteCategoryCommand;
import org.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdCommand;
import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdUseCase;
import org.fullcycle.admin.catalog.application.category.update.UpdateCategoryCommand;
import org.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.api.CategoryAPI;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import org.fullcycle.admin.catalog.infrastructure.category.models.GetCategoryApiOutput;
import org.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryApiInput;
import org.fullcycle.admin.catalog.infrastructure.category.presenters.CategoryApiPresenter;
import org.fullcycle.admin.catalog.infrastructure.utils.UriUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CategoryController implements CategoryAPI {

    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
        final GetCategoryByIdUseCase getCategoryByIdUseCase,
        final CreateCategoryUseCase createCategoryUseCase,
        final UpdateCategoryUseCase updateCategoryUseCase,
        final DeleteCategoryUseCase deleteCategoryUseCase
    ) {
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase, "'GetCategoryByIdUseCase' cannot be null");
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase, "'CreateCategoryUseCase' cannot be null");
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase, "'UpdateCategoryUseCase' cannot be null");
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase, "'DeleteCategoryUseCase' cannot be null");
    }

    @Override
    public Pagination<?> listCategories(final String search, final int page, final int perPage, final String sort, final String direction) {
        return null;
    }

    @Override
    public GetCategoryApiOutput getCategoryById(final String id) {
        final var command = GetCategoryByIdCommand.with(id);
        return CategoryApiPresenter.present(this.getCategoryByIdUseCase.execute(command));
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryApiInput input) {
        final var command = CreateCategoryCommand.with(
            input.name(),
            input.description(),
            input.active() != null ? input.active() : true
        );

        return this.createCategoryUseCase.execute(command)
            .fold(
                onError -> ResponseEntity.unprocessableEntity().body(onError),
                onSuccess -> ResponseEntity.created(UriUtils.buildAndExpandResourceId(onSuccess.id())).body(onSuccess)
            );
    }

    @Override
    public ResponseEntity<?> updateCategory(final String id, final UpdateCategoryApiInput input) {
        final var command = UpdateCategoryCommand.with(
            id,
            input.name(),
            input.description(),
            input.active() != null ? input.active() : true
        );

        return this.updateCategoryUseCase.execute(command)
            .fold(
                onError -> ResponseEntity.unprocessableEntity().body(onError),
                ResponseEntity::ok
            );
    }

    @Override
    public void deleteCategoryById(final String id) {
        final var command = DeleteCategoryCommand.with(id);
        this.deleteCategoryUseCase.execute(command);
    }

}
