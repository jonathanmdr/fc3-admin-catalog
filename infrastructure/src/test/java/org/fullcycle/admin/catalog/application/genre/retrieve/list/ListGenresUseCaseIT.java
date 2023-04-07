package org.fullcycle.admin.catalog.application.genre.retrieve.list;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@IntegrationTest
class ListGenresUseCaseIT {

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private DefaultListGenresUseCase useCase;

    @Test
    void givenAValidQuery_whenCallsListGenres_thenReturnGenres() {
        final var action = Genre.newGenre("Action", true);
        final var adventure = Genre.newGenre("Adventure", true);
        final var expectedGenres = List.of(
            action,
            adventure
        );

        genreGateway.create(action);
        genreGateway.create(adventure);

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

        final var listGenresCommand = ListGenresCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.useCase.execute(listGenresCommand);

        assertThat(actual).isEqualTo(expectedResult);
        assertThat(actual.items()).hasSize(expectedItemsCount);
        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedGenres.size());

        verify(genreGateway, atLeastOnce()).findAll(searchQuery);
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

        final var listGenresCommand = ListGenresCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = this.useCase.execute(listGenresCommand);

        assertThat(actual).isEqualTo(expectedResult);
        assertThat(actual.items()).hasSize(expectedItemsCount);
        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedGenres.size());

        verify(genreGateway, atLeastOnce()).findAll(searchQuery);
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

        when(genreGateway.findAll(searchQuery))
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

        assertThat(actual.getMessage()).isEqualTo(expectedErrorMessage);

        verify(genreGateway, atLeastOnce()).findAll(searchQuery);
    }

}
