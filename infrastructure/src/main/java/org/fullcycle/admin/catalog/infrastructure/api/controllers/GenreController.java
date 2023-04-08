package org.fullcycle.admin.catalog.infrastructure.api.controllers;

import org.fullcycle.admin.catalog.application.genre.create.CreateGenreCommand;
import org.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.delete.DeleteGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdCommand;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenresCommand;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenresUseCase;
import org.fullcycle.admin.catalog.application.genre.update.UpdateGenreCommand;
import org.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.api.GenreAPI;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.GetGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.ListGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.presenters.GenreApiPresenter;
import org.fullcycle.admin.catalog.infrastructure.utils.UriUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final ListGenresUseCase listGenresUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final CreateGenreUseCase createGenreUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;

    public GenreController(
        final ListGenresUseCase listGenresUseCase,
        final GetGenreByIdUseCase getGenreByIdUseCase,
        final CreateGenreUseCase createGenreUseCase,
        final UpdateGenreUseCase updateGenreUseCase,
        final DeleteGenreUseCase deleteGenreUseCase
    ) {
        this.listGenresUseCase = Objects.requireNonNull(listGenresUseCase, "'ListGenresUseCase' cannot be null");
        this.getGenreByIdUseCase = Objects.requireNonNull(getGenreByIdUseCase, "'GetGenreByIdUseCase' cannot be null");
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase, "'CreateGenreUseCase' cannot be null");
        this.updateGenreUseCase = Objects.requireNonNull(updateGenreUseCase, "'UpdateGenreUseCase' cannot be null");
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase, "'DeleteGenreUseCase' cannot be null");
    }

    @Override
    public Pagination<ListGenreResponse> listGenres(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String direction
    ) {
        final var command = ListGenresCommand.with(
            page,
            perPage,
            search,
            sort,
            direction
        );

        return this.listGenresUseCase.execute(command)
            .map(GenreApiPresenter::present);
    }

    @Override
    public GetGenreResponse getGenreById(final String id) {
        final var command = GetGenreByIdCommand.with(id);
        return GenreApiPresenter.present(this.getGenreByIdUseCase.execute(command));
    }

    @Override
    public ResponseEntity<CreateGenreResponse> createGenre(final CreateGenreRequest input) {
        final var command = CreateGenreCommand.with(
            input.name(),
            input.active() != null ? input.active() : Boolean.TRUE,
            input.categories()
        );

        final var output = this.createGenreUseCase.execute(command);
        return ResponseEntity.created(UriUtils.buildAndExpandResourceId(output.id()))
            .body(GenreApiPresenter.present(output));
    }

    @Override
    public ResponseEntity<UpdateGenreResponse> updateGenre(final String id, final UpdateGenreRequest input) {
        final var command = UpdateGenreCommand.with(
            id,
            input.name(),
            input.active() != null ? input.active() : Boolean.TRUE,
            input.categories()
        );

        final var output = this.updateGenreUseCase.execute(command);
        return ResponseEntity.ok(GenreApiPresenter.present(output));
    }

    @Override
    public void deleteGenreById(final String id) {
        this.deleteGenreUseCase.execute(id);
    }

}
