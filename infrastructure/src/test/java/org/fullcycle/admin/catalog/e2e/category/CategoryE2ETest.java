package org.fullcycle.admin.catalog.e2e.category;

import org.fullcycle.admin.catalog.E2ETest;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryResponse;
import org.fullcycle.admin.catalog.infrastructure.category.models.GetCategoryResponse;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
class CategoryE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
        .withDatabaseName("admin_catalog_videos")
        .withUsername("root")
        .withPassword("123456");

    @DynamicPropertySource
    public static void setDataSourceProperties(final DynamicPropertyRegistry registry) {
        final var privatePort = 3306;
        final var exposedPort = MYSQL_CONTAINER.getMappedPort(privatePort);
        System.out.printf("MariaDB container is running on port %s\n", exposedPort);
        registry.add("mariadb.port", () -> exposedPort);
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, categoryRepository.count());

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var createCategoryResponse = givenACategory(expectedName, expectedDescription, expectedIsActive);
        final var getCategoryResponse = retrieveACategoryById(createCategoryResponse.id());

        assertEquals(createCategoryResponse.id(), getCategoryResponse.id());
        assertEquals(expectedName, getCategoryResponse.name());
        assertEquals(expectedDescription, getCategoryResponse.description());
        assertEquals(expectedIsActive, getCategoryResponse.active());
        assertNotNull(getCategoryResponse.createdAt());
        assertNotNull(getCategoryResponse.updatedAt());
        assertNull(getCategoryResponse.deletedAt());

        assertEquals(1, categoryRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 1)
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
            .andExpect(jsonPath("$.items[0].description", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        listCategories(1, 1)
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")))
            .andExpect(jsonPath("$.items[0].description", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        listCategories(2, 1)
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Séries")))
            .andExpect(jsonPath("$.items[0].description", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        listCategories(3, 1)
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));

        assertEquals(3, categoryRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0, 1, "fi")
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")))
            .andExpect(jsonPath("$.items[0].description", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        assertEquals(3, categoryRepository.count());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", "C", true);
        givenACategory("Documentários", "Z", true);
        givenACategory("Séries", "A", true);

        listCategories(0, 3, "", "description", "desc")
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
            .andExpect(jsonPath("$.items[0].description", equalTo("Z")))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[1].name", equalTo("Filmes")))
            .andExpect(jsonPath("$.items[1].description", equalTo("C")))
            .andExpect(jsonPath("$.items[1].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[1].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[1].deleted_at", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[2].name", equalTo("Séries")))
            .andExpect(jsonPath("$.items[2].description", equalTo("A")))
            .andExpect(jsonPath("$.items[2].is_active", equalTo(true)))
            .andExpect(jsonPath("$.items[2].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[2].deleted_at", IsNull.nullValue()));

        assertEquals(3, categoryRepository.count());
    }

    private ResultActions listCategories(final int page, final int perPage)throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(final int page, final int perPage, final String search)throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    private ResultActions listCategories(
        final int page,
        final int perPage,
        final String search,
        final String sort,
        final String direction
    ) throws Exception {
        final var request = get("/categories")
            .queryParam("page", String.valueOf(page))
            .queryParam("perPage", String.valueOf(perPage))
            .queryParam("search", search)
            .queryParam("sort", sort)
            .queryParam("direction", direction)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        return this.mvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private CreateCategoryResponse givenACategory(final String name, final String description, final boolean active) throws Exception {
        final var requestBody = new CreateCategoryRequest(name, description, active);

        final var request = post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(Json.writeValueAsString(requestBody));

        final var response = this.mvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(header().string(HttpHeaders.LOCATION, StringContains.containsString("/categories/")))
            .andReturn()
            .getResponse().getContentAsString();

        return Json.readValue(response, CreateCategoryResponse.class);
    }

    private GetCategoryResponse retrieveACategoryById(final String categoryId) throws Exception {
        final var request = get("/categories/{id}", categoryId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse().getContentAsString();

        return Json.readValue(response, GetCategoryResponse.class);
    }

}
