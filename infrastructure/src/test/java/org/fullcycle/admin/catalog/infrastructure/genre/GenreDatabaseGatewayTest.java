package org.fullcycle.admin.catalog.infrastructure.genre;

import org.fullcycle.admin.catalog.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.infrastructure.category.CategoryDatabaseGateway;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
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
        final var movies = categoryDatabaseGateway.create(
            Category.newCategory("Movies", "Movies", true)
        );

        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(movies.getId());

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories);

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var actual = genreDatabaseGateway.create(genre);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).containsExactlyInAnyOrderElementsOf(expectedCategories);
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
        assertThat(actual.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidGenreWithoutCategories_whenCallsUpdateGenreWithCategories_shouldUpdateGenre() {
        final var movies = categoryDatabaseGateway.create(
            Category.newCategory("Movies", "Movies", true)
        );
        final var series = categoryDatabaseGateway.create(
            Category.newCategory("Series", "Series", true)
        );
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(movies.getId(), series.getId());

        final var genre = Genre.newGenre("Name to update", expectedIsActive);

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var savedGenre = genreRepository.saveAndFlush(GenreJpaEntity.from(genre)).toAggregate();

        assertThat(savedGenre.getName()).isEqualTo(genre.getName());
        assertThat(savedGenre.getCategories().size()).isZero();

        final var genreToUpdate = Genre.with(genre)
            .update(expectedName, expectedIsActive, expectedCategories);
        final var actual = genreDatabaseGateway.update(genreToUpdate);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidGenreWithCategories_whenCallsUpdateGenreClearingCategories_shouldUpdateGenre() {
        final var movies = categoryDatabaseGateway.create(
            Category.newCategory("Movies", "Movies", true)
        );
        final var series = categoryDatabaseGateway.create(
            Category.newCategory("Series", "Series", true)
        );
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var genre = Genre.newGenre("Name to update", expectedIsActive);
        genre.addCategories(
            List.of(
                movies.getId(),
                series.getId()
            )
        );

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var savedGenre = genreRepository.saveAndFlush(GenreJpaEntity.from(genre)).toAggregate();

        assertThat(savedGenre.getName()).isEqualTo(genre.getName());
        assertThat(savedGenre.getCategories().size()).isEqualTo(2);

        final var genreToUpdate = Genre.with(genre)
            .update(expectedName, expectedIsActive, Collections.emptyList());
        final var actual = genreDatabaseGateway.update(genreToUpdate);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidGenreInactive_whenCallsUpdateGenreActivating_shouldUpdateGenre() {
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var genre = Genre.newGenre(expectedName, false);

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var savedGenre = genreRepository.saveAndFlush(GenreJpaEntity.from(genre)).toAggregate();

        assertThat(savedGenre.isActive()).isFalse();
        assertThat(savedGenre.getDeletedAt()).isNotNull();

        final var genreToUpdate = Genre.with(genre).activate();
        final var actual = genreDatabaseGateway.update(genreToUpdate);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidActiveGenre_whenCallsUpdateGenreDeactivating_shouldUpdateGenre() {
        final var expectedName = "Action";
        final var expectedIsActive = false;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var genre = Genre.newGenre(expectedName, true);

        final var expectedId = genre.getId();

        assertThat(genreRepository.count()).isZero();

        final var savedGenre = genreRepository.saveAndFlush(GenreJpaEntity.from(genre)).toAggregate();

        assertThat(savedGenre.isActive()).isTrue();
        assertThat(savedGenre.getDeletedAt()).isNull();

        final var genreToUpdate = Genre.with(genre).deactivate();
        final var actual = genreDatabaseGateway.update(genreToUpdate);

        assertThat(genreRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNotNull();

        final var actualInDatabase = genreRepository.findById(genre.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected genre not found in database"));

        assertThat(actualInDatabase.getName()).isEqualTo(expectedName);
        assertThat(actualInDatabase.isActive()).isEqualTo(expectedIsActive);
        assertThat(actualInDatabase.getCategoryIDs()).containsExactlyInAnyOrderElementsOf(expectedCategories);
        assertThat(actualInDatabase.getCreatedAt()).isEqualTo(genreToUpdate.getCreatedAt());
        assertThat(actualInDatabase.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actualInDatabase.getDeletedAt()).isEqualTo(genreToUpdate.getDeletedAt());
        assertThat(actualInDatabase.getDeletedAt()).isNotNull();
    }

    @Test
    void givenAPrePersistedGenre_whenCallsDelete_shouldDeleteGenre() {
        final var genre = Genre.newGenre("Action", true);

        assertThat(genreRepository.count()).isZero();
        genreRepository.save(GenreJpaEntity.from(genre));
        assertThat(genreRepository.count()).isOne();

        genreDatabaseGateway.deleteById(genre.getId());
        assertThat(genreRepository.count()).isZero();
    }

    @Test
    void givenANotFoundGenre_whenCallsDelete_shouldDoNothing() {
        assertThat(genreRepository.count()).isZero();
        genreDatabaseGateway.deleteById(GenreID.unique());
        assertThat(genreRepository.count()).isZero();
    }

}
