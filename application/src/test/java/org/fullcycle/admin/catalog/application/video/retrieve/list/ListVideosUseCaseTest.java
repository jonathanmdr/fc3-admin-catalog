package org.fullcycle.admin.catalog.application.video.retrieve.list;

import org.fullcycle.admin.catalog.application.Fixtures;
import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoSearchQuery;
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

class ListVideosUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway gateway;

    @InjectMocks
    private DefaultListVideosUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    void givenAValidQuery_whenCallsListVideos_thenReturnVideos() {
        final var expectedVideos = List.of(
                Fixtures.VideoFixture.video(),
                Fixtures.VideoFixture.video()
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
                expectedVideos
        );

        final var searchQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var expectedResult = expectedPagination.map(ListVideosOutput::from);

        when(gateway.findAll(searchQuery)).thenReturn(expectedPagination);

        final var listVideosCommand = ListVideosCommand.with(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var actual = this.useCase.execute(listVideosCommand);

        assertEquals(expectedResult, actual);
        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedVideos.size(), actual.total());

        verify(gateway, atLeastOnce()).findAll(searchQuery);
    }

    @Test
    void givenAValidQuery_whenHasNoResults_thenReturnEmptyVideos() {
        final var expectedVideos = Collections.<Video>emptyList();

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
                expectedVideos
        );

        final var searchQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var expectedResult = expectedPagination.map(ListVideosOutput::from);

        when(gateway.findAll(searchQuery)).thenReturn(expectedPagination);

        final var listVideosCommand = ListVideosCommand.with(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var actual = this.useCase.execute(listVideosCommand);

        assertEquals(expectedResult, actual);
        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(0, actual.total());

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

        final var searchQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        when(gateway.findAll(searchQuery))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var listVideosCommand = ListVideosCommand.with(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection
        );

        final var actual = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(listVideosCommand)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());

        verify(gateway, atLeastOnce()).findAll(searchQuery);
    }

}
