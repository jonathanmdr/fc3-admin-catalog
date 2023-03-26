package org.fullcycle.admin.catalog.application.genre.retrieve.list;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListGenresUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway gateway;

    @InjectMocks
    private DefaultListGenresUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    void givenAValidQuery_whenCallsListGenres_thenReturnGenres() {
        final var expectedGenres = List.of(
            Genre.newGenre("Action", true),
            Genre.newGenre("Adventure", true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 2;

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedGenres
        );

        final var searchQuery = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var expectedResult = expectedPagination.map(ListGenresOutput::from);

        when(gateway.findAll(searchQuery)).thenReturn(expectedPagination);

        final var listGenresCommand = ListGenresCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.useCase.execute(listGenresCommand);

        assertEquals(expectedResult, actual);
        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedGenres.size(), actual.total());

        verify(gateway, atLeastOnce()).findAll(searchQuery);
    }

    @Test
    void givenAValidQuery_whenHasNoResults_thenReturnEmptyGenres() {
        final var expectedGenres = Collections.<Genre>emptyList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedGenres
        );

        final var searchQuery = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var expectedResult = expectedPagination.map(ListGenresOutput::from);

        when(gateway.findAll(searchQuery)).thenReturn(expectedPagination);

        final var listGenresCommand = ListGenresCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.useCase.execute(listGenresCommand);

        assertEquals(expectedResult, actual);
        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedGenres.size(), actual.total());

        verify(gateway, atLeastOnce()).findAll(searchQuery);
    }

    @Test
    void givenAValidQuery_whenGatewayThrowsUnexpectedException_thenThrowsException() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway unexpected error";

        final var searchQuery = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        when(gateway.findAll(searchQuery))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var listGenresCommand = ListGenresCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(listGenresCommand)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());

        verify(gateway, atLeastOnce()).findAll(searchQuery);
    }
}
