package org.fullcycle.admin.catalog.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fullcycle.admin.catalog.ApiTest;
import org.fullcycle.admin.catalog.application.category.create.CreateCategoryOutput;
import org.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdOutput;
import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoriesUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoryOutput;
import org.fullcycle.admin.catalog.application.category.update.UpdateCategoryOutput;
import org.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import org.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryRequest;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiTest(controllers = CategoryAPI.class)
class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateCategory_thenReturnCategoryId() throws Exception {
        final var expectedId = CategoryID.unique().getValue();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
            .thenReturn(right(CreateCategoryOutput.from(expectedId)));

        final var request = post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(input));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, containsString(format("/categories/%s", expectedId))))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(createCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAInvalidCommand_whenCallsCreateCategory_thenShouldThrowNotificationWithDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
            .thenReturn(left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var request = post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(input));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(HttpHeaders.LOCATION, nullValue()))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAInvalidCommand_whenCallsCreateCategory_thenShouldThrowDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var input = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
            .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(input));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(HttpHeaders.LOCATION, nullValue()))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAValidId_whenCallGetCategoryById_thenReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId().getValue();

        when(getCategoryByIdUseCase.execute(any()))
            .thenReturn(GetCategoryByIdOutput.from(category));

        final var request = get("/categories/{id}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)))
            .andExpect(jsonPath("$.name", equalTo(expectedName)))
            .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
            .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
            .andExpect(jsonPath("$.created_at", equalTo(category.getCreatedAt().toString())))
            .andExpect(jsonPath("$.updated_at", equalTo(category.getUpdatedAt().toString())))
            .andExpect(jsonPath("$.deleted_at", IsNull.nullValue()));

        verify(getCategoryByIdUseCase, times(1))
            .execute(argThat(cmd -> Objects.equals(expectedId, cmd.id())));
    }

    @Test
    void givenAInvalidId_whenCallGetCategoryById_thenReturnNotFoundException() throws Exception {
        final var expectedId = CategoryID.unique();
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());

        when(getCategoryByIdUseCase.execute(any()))
            .thenThrow(NotFoundException.with(Category.class, expectedId));

        final var request = get("/categories/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getCategoryByIdUseCase, times(1))
            .execute(argThat(cmd -> Objects.equals(expectedId.getValue(), cmd.id())));
    }

    @Test
    void givenAValidCommand_whenCallUpdateCategory_thenReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = CategoryID.unique().getValue();

        when(updateCategoryUseCase.execute(any()))
            .thenReturn(right(UpdateCategoryOutput.from(expectedId)));

        final var body = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(body));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId, cmd.id())
                && Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAInvalidName_whenCallUpdateCategory_thenShouldReturnDomainException() throws Exception {
        final var expectedId = CategoryID.unique();
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        when(updateCategoryUseCase.execute(any()))
            .thenReturn(left(NotificationHandler.create(new Error(expectedErrorMessage))));

        final var body = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(body));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id())
                && Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenACommandWithInvalidId_whenCallUpdateCategory_thenReturnNotFoundException() throws Exception {
        final var expectedId = CategoryID.unique();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());

        when(updateCategoryUseCase.execute(any()))
            .thenThrow(NotFoundException.with(Category.class, expectedId));

        final var body = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(body));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id())
                && Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAValidId_whenCallDeleteCategory_shouldBeReturnNoContent() throws Exception {
        final var expectedId = CategoryID.unique();

        doNothing()
            .when(deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id())
            ));
    }

    @Test
    void givenValidParams_whenCallsListCategories_shouldReturnCategories() throws Exception {
        final var category = Category.newCategory("Movies", null, true);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = singletonList(ListCategoryOutput.from(category));

        when(listCategoriesUseCase.execute(any()))
            .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/categories")
            .queryParam("page", String.valueOf(expectedPage))
            .queryParam("perPage", String.valueOf(expectedPerPage))
            .queryParam("sort", expectedSort)
            .queryParam("direction", expectedDirection)
            .queryParam("search", expectedTerms)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
            .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
            .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
            .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
            .andExpect(jsonPath("$.items[0].id", equalTo(category.getId().getValue())))
            .andExpect(jsonPath("$.items[0].name", equalTo(category.getName())))
            .andExpect(jsonPath("$.items[0].description", IsNull.nullValue()))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(category.isActive())))
            .andExpect(jsonPath("$.items[0].created_at", equalTo(category.getCreatedAt().toString())))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        verify(listCategoriesUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedPage, cmd.page())
                && Objects.equals(expectedPerPage, cmd.perPage())
                && Objects.equals(expectedTerms, cmd.terms())
                && Objects.equals(expectedSort, cmd.sort())
                && Objects.equals(expectedDirection, cmd.direction())
            ));
    }

}
