package org.fullcycle.admin.catalog.infrastructure.genre;

import org.fullcycle.admin.catalog.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.infrastructure.category.CategoryDatabaseGateway;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DatabaseGatewayIntegrationTest
class GenreDatabaseGatewayTest {

    @Autowired
    private CategoryDatabaseGateway categoryDatabaseGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreDatabaseGateway genreDatabaseGateway;

    @Test
    void givenAValidGenre_whenCallsCreateGenre_shouldPersistGenre() {
        final var movie = categoryDatabaseGateway.create(
            Category.newCategory("Movie", "Movie", true)
        );

        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(movie.getId());

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories);

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var actual = genreDatabaseGateway.create(genre);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).isEqualTo(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).isEqualTo(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidGenreWithoutCategories_whenCallsCreateGenre_shouldPersistGenre() {
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var actual = genreDatabaseGateway.create(genre);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).isEqualTo(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).isEqualTo(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNull();
    }

}
