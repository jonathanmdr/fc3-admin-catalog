package org.fullcycle.admin.catalog.e2e.genre;

import org.fullcycle.admin.catalog.E2ETest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@E2ETest
@Testcontainers
class GenreE2ETest extends GenreMockMvcDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CategoryGateway categoryGateway;

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
        .withDatabaseName("admin_catalog_videos")
        .withUsername("root")
        .withPassword("root");

    @DynamicPropertySource
    public static void setDataSourceProperties(final DynamicPropertyRegistry registry) {
        final var privatePort = 3306;
        final var exposedPort = MYSQL_CONTAINER.getMappedPort(privatePort);
        System.out.printf("MySQL container is running on port %s\n", exposedPort);
        registry.add("mysql.port", () -> exposedPort);
    }

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValues() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        final var documentary = Category.newCategory("Documentary", "Documentary", true);
        final var series = Category.newCategory("Series", "Series", true);

        categoryGateway.create(documentary);
        categoryGateway.create(series);

        final var expectedName = "Action";
        final var expectedCategories = List.of(
            documentary.getId().getValue(),
            series.getId().getValue()
        );
        final var expectedIsActive = true;

        final var output = givenAGenre(expectedName, expectedCategories, expectedIsActive);
        final var actual = retrieveAGenreById(output.id());

        assertThat(actual.id()).isEqualTo(output.id());
        assertThat(actual.name()).isEqualTo(expectedName);
        assertThat(actual.categories()).hasSize(2);
        assertThat(actual.categories()).containsAll(expectedCategories);
        assertThat(actual.active()).isEqualTo(expectedIsActive);
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();
        assertThat(actual.deletedAt()).isNull();

        assertEquals(1, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllGenres() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        final var action = givenAGenre("Action", Collections.emptyList(), true);
        final var drama = givenAGenre("Drama", Collections.emptyList(), true);
        final var comedy = givenAGenre("Comedy", Collections.emptyList(), true);

        listGenres(0, 1)
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(action.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Action")))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        listGenres(1, 1)
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(comedy.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Comedy")))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        listGenres(2, 1)
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(drama.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Drama")))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        listGenres(3, 1)
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));

        assertEquals(3, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllGenres() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        givenAGenre("Action", Collections.emptyList(), true);
        givenAGenre("Drama", Collections.emptyList(), true);
        final var comedy = givenAGenre("Comedy", Collections.emptyList(), true);

        listGenres(0, 1, "co")
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(comedy.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Comedy")))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        assertEquals(3, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllGenresByNameDesc() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        final var drama = givenAGenre("Drama", Collections.emptyList(), true);
        final var action = givenAGenre("Action", Collections.emptyList(), true);
        final var comedy = givenAGenre("Comedy", Collections.emptyList(), true);

        listGenres(0, 3, "", "name", "desc")
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].id", equalTo(drama.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Drama")))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[1].id", equalTo(comedy.id())))
            .andExpect(jsonPath("$.items[1].name", equalTo("Comedy")))
            .andExpect(jsonPath("$.items[1].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[1].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[1].deleted_at", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[2].id", equalTo(action.id())))
            .andExpect(jsonPath("$.items[2].name", equalTo("Action")))
            .andExpect(jsonPath("$.items[2].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[2].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[2].deleted_at", IsNull.nullValue()));

        assertEquals(3, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToToGetAGenreByIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        final var documentary = Category.newCategory("Documentary", "Documentary", true);
        final var series = Category.newCategory("Series", "Series", true);

        categoryGateway.create(documentary);
        categoryGateway.create(series);

        final var expectedName = "Action";
        final var expectedCategories = List.of(
            documentary.getId().getValue(),
            series.getId().getValue()
        );
        final var expectedIsActive = true;

        final var createdGenre = givenAGenre(expectedName, expectedCategories, expectedIsActive);
        final var actual = retrieveAGenreById(createdGenre.id());
        final var expected = genreRepository.findById(createdGenre.id())
                .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.id()).isEqualTo(expected.getId());
        assertThat(actual.name()).isEqualTo(expectedName);
        assertThat(actual.categories()).hasSize(2);
        assertThat(actual.categories()).containsAll(expectedCategories);
        assertThat(actual.active()).isEqualTo(expectedIsActive);
        assertThat(actual.createdAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.updatedAt()).isEqualTo(expected.getUpdatedAt());
        assertThat(actual.deletedAt()).isEqualTo(expected.getDeletedAt());

        assertEquals(1, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundGenre() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        retrieveANotFoundGenreById(GenreID.unique().getValue());

        assertEquals(0, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateAGenreByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        final var createdGenre = givenAGenre("Foo", Collections.emptyList(), false);
        var actual = retrieveAGenreById(createdGenre.id());

        assertThat(actual.id()).isEqualTo(createdGenre.id());
        assertThat(actual.name()).isEqualTo("Foo");
        assertThat(actual.categories()).isEmpty();
        assertThat(actual.active()).isFalse();
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();
        assertThat(actual.deletedAt()).isNotNull();

        assertEquals(1, genreRepository.count());

        final var documentary = Category.newCategory("Documentary", "Documentary", true);
        final var series = Category.newCategory("Series", "Series", true);

        categoryGateway.create(documentary);
        categoryGateway.create(series);

        final var expectedName = "Action";
        final var expectedCategories = List.of(
            documentary.getId().getValue(),
            series.getId().getValue()
        );
        final var expectedIsActive = true;

        final var updatedGenre = updateAGenre(
            createdGenre.id(),
            expectedName,
            expectedCategories,
            expectedIsActive
        );
        actual = retrieveAGenreById(createdGenre.id());

        assertThat(actual.id()).isEqualTo(updatedGenre.id());
        assertThat(actual.name()).isEqualTo(expectedName);
        assertThat(actual.categories()).hasSize(2);
        assertThat(actual.categories()).containsAll(expectedCategories);
        assertThat(actual.active()).isTrue();
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();
        assertThat(actual.deletedAt()).isNull();

        assertEquals(1, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteAGenre() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        final var genreCreated = givenAGenre("Action", Collections.emptyList(), true);

        assertEquals(1, genreRepository.count());

        deleteAGenre(genreCreated.id());

        assertEquals(0, genreRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteANotFoundGenre() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, genreRepository.count());

        deleteAGenre(GenreID.unique().getValue());

        assertEquals(0, genreRepository.count());
    }

}
