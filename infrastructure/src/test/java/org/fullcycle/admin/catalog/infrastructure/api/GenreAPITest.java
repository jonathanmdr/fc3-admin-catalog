package org.fullcycle.admin.catalog.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fullcycle.admin.catalog.ApiTest;
import org.fullcycle.admin.catalog.application.genre.create.CreateGenreOutput;
import org.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.delete.DeleteGenreUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdOutput;
import org.fullcycle.admin.catalog.application.genre.retrieve.get.GetGenreByIdUseCase;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenresOutput;
import org.fullcycle.admin.catalog.application.genre.retrieve.list.ListGenresUseCase;
import org.fullcycle.admin.catalog.application.genre.update.UpdateGenreOutput;
import org.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreRequest;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

@ApiTest(controllers = GenreAPI.class)
class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListGenresUseCase listGenresUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateGenre_thenReturnGenreId() throws Exception {
        final var expectedId = GenreID.unique().getValue();
        final var expectedName = "Action";
        final var expectedCategories = List.of(
            CategoryID.unique().getValue(),
            CategoryID.unique().getValue()
        );
        final var expectedIsActive = true;

        final var input = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
            .thenReturn(CreateGenreOutput.from(expectedId));

        final var request = post("/genres")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(input));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, containsString(format("/genres/%s", expectedId))))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(createGenreUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedCategories, cmd.categories())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAInvalidCommand_whenCallsCreateGenre_thenShouldThrowDomainException() throws Exception {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var input = new CreateGenreRequest(expectedName, Collections.emptyList(), expectedIsActive);

        when(createGenreUseCase.execute(any()))
            .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = post("/genres")
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

        verify(createGenreUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                && cmd.categories().isEmpty()
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAValidId_whenCallGetGenreById_thenReturnGenreId() throws Exception {
        final var expectedName = "Movies";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.unique());
        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId().getValue();

        genre.addCategories(expectedCategories);

        when(getGenreByIdUseCase.execute(any()))
            .thenReturn(GetGenreByIdOutput.from(genre));

        final var request = get("/genres/{id}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(expectedId)))
            .andExpect(jsonPath("$.name", equalTo(expectedName)))
            .andExpect(jsonPath("$.categories_ids", hasSize(1)))
            .andExpect(jsonPath("$.categories_ids", equalTo(expectedCategories.stream().map(CategoryID::getValue).toList())))
            .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
            .andExpect(jsonPath("$.created_at", equalTo(genre.getCreatedAt().toString())))
            .andExpect(jsonPath("$.updated_at", equalTo(genre.getUpdatedAt().toString())))
            .andExpect(jsonPath("$.deleted_at", IsNull.nullValue()));

        verify(getGenreByIdUseCase, times(1))
            .execute(argThat(cmd -> Objects.equals(expectedId, cmd.id())));
    }

    @Test
    void givenAInvalidId_whenCallGetGenreById_thenReturnNotFoundException() throws Exception {
        final var expectedId = GenreID.unique();
        final var expectedErrorMessage = "Genre with ID %s was not found".formatted(expectedId.getValue());

        when(getGenreByIdUseCase.execute(any()))
            .thenThrow(NotFoundException.with(Genre.class, expectedId));

        final var request = get("/genres/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getGenreByIdUseCase, times(1))
            .execute(argThat(cmd -> Objects.equals(expectedId.getValue(), cmd.id())));
    }

    @Test
    void givenAValidCommand_whenCallUpdateGenre_thenReturnGenreId() throws Exception {
        final var expectedName = "Movies";
        final var expectedIsActive = true;
        final var expectedFirstCategory = CategoryID.unique();
        final var expectedSecondCategory = CategoryID.unique();
        final var expectedCategories = List.of(expectedFirstCategory.getValue(), expectedSecondCategory.getValue());
        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(List.of(expectedFirstCategory, expectedSecondCategory));

        when(updateGenreUseCase.execute(any()))
            .thenReturn(UpdateGenreOutput.from(genre));

        final var body = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        final var request = put("/genres/{id}", genre.getId().getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(body));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id", equalTo(genre.getId().getValue())));

        verify(updateGenreUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(genre.getId().getValue(), cmd.id())
                && Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedCategories, cmd.categories())
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAInvalidName_whenCallUpdateGenre_thenShouldReturnDomainException() throws Exception {
        final var expectedId = GenreID.unique();
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        when(updateGenreUseCase.execute(any()))
            .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var body = new UpdateGenreRequest(expectedName, Collections.emptyList(), expectedIsActive);

        final var request = put("/genres/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(body));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateGenreUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id())
                && Objects.equals(expectedName, cmd.name())
                && cmd.categories().isEmpty()
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenACommandWithInvalidId_whenCallUpdateGenre_thenReturnNotFoundException() throws Exception {
        final var expectedId = GenreID.unique();
        final var expectedName = "Movies";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Genre with ID %s was not found".formatted(expectedId.getValue());

        when(updateGenreUseCase.execute(any()))
            .thenThrow(NotFoundException.with(Genre.class, expectedId));

        final var body = new UpdateGenreRequest(expectedName, Collections.emptyList(), expectedIsActive);

        final var request = put("/genres/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.objectMapper.writeValueAsString(body));

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateGenreUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id())
                && Objects.equals(expectedName, cmd.name())
                && cmd.categories().isEmpty()
                && Objects.equals(expectedIsActive, cmd.isActive())
            ));
    }

    @Test
    void givenAValidId_whenCallDeleteGenre_shouldBeReturnNoContent() throws Exception {
        final var expectedId = GenreID.unique();

        doNothing()
            .when(deleteGenreUseCase).execute(any());

        final var request = delete("/genres/{id}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(deleteGenreUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd)
            ));
    }

    @Test
    void givenValidParams_whenCallsListCategories_shouldReturnCategories() throws Exception {
        final var genre = Genre.newGenre("Movies", true);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = singletonList(ListGenresOutput.from(genre));

        when(listGenresUseCase.execute(any()))
            .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/genres")
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
            .andExpect(jsonPath("$.items[0].id", equalTo(genre.getId().getValue())))
            .andExpect(jsonPath("$.items[0].name", equalTo(genre.getName())))
            .andExpect(jsonPath("$.items[0].is_active", equalTo(genre.isActive())))
            .andExpect(jsonPath("$.items[0].created_at", equalTo(genre.getCreatedAt().toString())))
            .andExpect(jsonPath("$.items[0].deleted_at", IsNull.nullValue()));

        verify(listGenresUseCase, times(1))
            .execute(argThat(cmd ->
                Objects.equals(expectedPage, cmd.page())
                && Objects.equals(expectedPerPage, cmd.perPage())
                && Objects.equals(expectedTerms, cmd.terms())
                && Objects.equals(expectedSort, cmd.sort())
                && Objects.equals(expectedDirection, cmd.direction())
            ));
    }

}
