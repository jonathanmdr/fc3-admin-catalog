package org.fullcycle.admin.catalog.application.genre.retrieve.get;

import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {

    private final GenreGateway gateway;

    public DefaultGetGenreByIdUseCase(final GenreGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public GetGenreByIdOutput execute(final GetGenreByIdCommand command) {
        final var genreId = GenreID.from(command.id());

        return this.gateway.findById(genreId)
            .map(GetGenreByIdOutput::from)
            .orElseThrow(genreNotFound(genreId));
    }

    private Supplier<NotFoundException> genreNotFound(final GenreID id) {
        return () -> NotFoundException.with(Genre.class, id);
    }

}
