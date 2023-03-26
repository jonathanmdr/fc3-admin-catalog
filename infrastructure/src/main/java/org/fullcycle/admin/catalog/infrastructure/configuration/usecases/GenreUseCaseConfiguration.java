package org.fullcycle.admin.catalog.infrastructure.configuration.usecases;

import org.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.create.DefaultCreateGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.delete.DefaultDeleteGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.delete.DeleteGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.DefaultGetGenreByIdUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.DefaultListGenresUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenresUseCase;
import org.fullcycle.admin.catalog.application.genre.update.DefaultUpdateGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class GenreUseCaseConfiguration {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public GenreUseCaseConfiguration(
        final CategoryGateway categoryGateway,
        final GenreGateway genreGateway
    ) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Bean
    public ListGenresUseCase listGenresUseCase() {
        return new DefaultListGenresUseCase(genreGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase() {
        return new DefaultGetGenreByIdUseCase(genreGateway);
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase() {
        return new DefaultCreateGenreUseCase(categoryGateway, genreGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreUseCase() {
        return new DefaultUpdateGenreUseCase(categoryGateway, genreGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase() {
        return new DefaultDeleteGenreUseCase(genreGateway);
    }

}
