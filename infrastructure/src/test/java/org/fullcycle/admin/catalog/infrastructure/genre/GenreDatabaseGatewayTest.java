package org.fullcycle.admin.catalog.infrastructure.genre;

import org.fullcycle.admin.catalog.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.category.CategoryDatabaseGateway;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));
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

    @Test
    void givenAPrePersistedGenre_whenCallsFindById_shouldReturnGenre() {
        final var genre = Genre.newGenre("Action", true);

        assertThat(genreRepository.count()).isZero();
        genreRepository.saveAndFlush(GenreJpaEntity.from(genre));
        assertThat(genreRepository.count()).isOne();

        final var actual = genreDatabaseGateway.findById(genre.getId())
                .orElseThrow(() -> new IllegalStateException("Expected genre not found"));

        assertThat(actual.getId()).isEqualTo(genre.getId());
        assertThat(actual.getName()).isEqualTo(genre.getName());
        assertThat(actual.isActive()).isEqualTo(genre.isActive());
        assertThat(actual.getCategories()).isEmpty();
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genre.getDeletedAt());
        assertThat(actual.getDeletedAt()).isNull();
    }

    @Test
    void givenANotFoundGenre_whenCallsFindById_shouldReturnEmpty() {
        final var genre = Genre.newGenre("Action", true);

        assertThat(genreRepository.count()).isZero();

        final var actual = genreDatabaseGateway.findById(genre.getId());

        assertThat(genreRepository.count()).isZero();
        assertThat(actual).isEmpty();
    }

    @Test
    void givenAnEmptyGenres_whenCallsFindAll_shouldReturnsEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTotal = 0;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.genreDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isZero();
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "ac,0,10,1,1,Action",
            "dr,0,10,1,1,Drama",
            "com,0,10,1,1,Comedy",
            "sc,0,10,1,1,Science Fiction",
            "ho,0,10,1,1,Horror",
            "a,0,10,2,2,Action;Drama",
            "o,0,10,4,4,Action;Comedy;Horror;Science Fiction",
        }
    )
    void givenAValidTerms_whenCallsFindAll_shouldReturnsFilteredGenres(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedGenreName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var action = GenreJpaEntity.from(Genre.newGenre("Action", true));
        final var drama = GenreJpaEntity.from(Genre.newGenre("Drama", true));
        final var comedy = GenreJpaEntity.from(Genre.newGenre("Comedy", true));
        final var scienceFiction = GenreJpaEntity.from(Genre.newGenre("Science Fiction", true));
        final var horror = GenreJpaEntity.from(Genre.newGenre("Horror", true));

        assertThat(this.genreRepository.count()).isZero();
        this.genreRepository.saveAllAndFlush(
            List.of(
                action,
                drama,
                comedy,
                scienceFiction,
                horror
            )
        );
        assertThat(this.genreRepository.count()).isEqualTo(5);

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.genreDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedItemsCount);

        var index = 0;
        for (final var expectedName : expectedGenreName.split(";")) {
            assertThat(actual.items().get(index).getName()).isEqualTo(expectedName);
            index++;
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "name,asc,0,10,5,5,Action",
            "name,desc,0,10,5,5,Science Fiction",
            "createdAt,asc,0,10,5,5,Action",
            "createdAt,desc,0,10,5,5,Horror"
        }
    )
    void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnsFilteredGenres(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedGenreName
    ) {
        final var expectedTerms = "";

        final var action = GenreJpaEntity.from(Genre.newGenre("Action", true));
        final var drama = GenreJpaEntity.from(Genre.newGenre("Drama", true));
        final var comedy = GenreJpaEntity.from(Genre.newGenre("Comedy", true));
        final var scienceFiction = GenreJpaEntity.from(Genre.newGenre("Science Fiction", true));
        final var horror = GenreJpaEntity.from(Genre.newGenre("Horror", true));

        assertThat(this.genreRepository.count()).isZero();
        this.genreRepository.saveAllAndFlush(
            List.of(
                action,
                drama,
                comedy,
                scienceFiction,
                horror
            )
        );
        assertThat(this.genreRepository.count()).isEqualTo(5);

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.genreDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedItemsCount);
        assertThat(actual.items().get(0).getName()).isEqualTo(expectedGenreName);
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "0,2,2,5,Action;Comedy",
            "1,2,2,5,Drama;Horror",
            "2,2,1,5,Science Fiction"
        }
    )
    void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnsFilteredGenres(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedGenreName
    ) {
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var action = GenreJpaEntity.from(Genre.newGenre("Action", true));
        final var drama = GenreJpaEntity.from(Genre.newGenre("Drama", true));
        final var comedy = GenreJpaEntity.from(Genre.newGenre("Comedy", true));
        final var scienceFiction = GenreJpaEntity.from(Genre.newGenre("Science Fiction", true));
        final var horror = GenreJpaEntity.from(Genre.newGenre("Horror", true));

        assertThat(this.genreRepository.count()).isZero();
        this.genreRepository.saveAllAndFlush(
            List.of(
                action,
                drama,
                comedy,
                scienceFiction,
                horror
            )
        );
        assertThat(this.genreRepository.count()).isEqualTo(5);

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.genreDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedItemsCount);

        var index = 0;
        for (final var expectedName : expectedGenreName.split(";")) {
            assertThat(actual.items().get(index).getName()).isEqualTo(expectedName);
            index++;
        }
    }

    @Test
    void givenPrePersistedGenres_whenCallsExistsByIds_shouldReturnIds() {
        final var action = Genre.newGenre("Action", true);
        final var suspense = Genre.newGenre("Suspense", true);
        final var kids = Genre.newGenre("Kids", true);

        assertEquals(0, this.genreRepository.count());

        final var ids = this.genreRepository.saveAll(
                List.of(
                    GenreJpaEntity.from(action),
                    GenreJpaEntity.from(suspense),
                    GenreJpaEntity.from(kids)
                )
            )
            .stream()
            .map(GenreJpaEntity::getId)
            .map(GenreID::from)
            .toList();

        assertEquals(3, this.genreRepository.count());

        final var actual = this.genreDatabaseGateway.existsByIds(ids);

        assertThat(actual).hasSize(3);
        assertThat(actual).containsAll(ids);
    }

}
