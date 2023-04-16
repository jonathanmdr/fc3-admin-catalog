package org.fullcycle.admin.catalog.application.castmember.retrieve.list;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultListCastMembersUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            castMemberGateway
        );
    }

    @Test
    void givenAValidQuery_whenCallsListCastMembers_thenReturnCastMembers() {
        final var castMembers = List.of(
            CastMember.newMember("Vin Diesel", CastMemberType.ACTOR),
            CastMember.newMember("The Rock", CastMemberType.ACTOR)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            castMembers.size(),
            castMembers
        );

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(ListCastMembersOutput::from);

        when(castMemberGateway.findAll(query))
            .thenReturn(expectedPagination);

        final var command = ListCastMembersCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = useCase.execute(command);

        assertEquals(expectedResult, actual);
        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(castMembers.size(), actual.total());
    }

    @Test
    void givenAValidQuery_whenHasNoResults_thenReturnEmptyCategories() {
        final var castMembers = Collections.<CastMember>emptyList();

        final var expectedPage = 0;
        final var expectedTotal = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedTotal,
            castMembers
        );

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(ListCastMembersOutput::from);

        when(castMemberGateway.findAll(query))
            .thenReturn(expectedPagination);

        final var command = ListCastMembersCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = useCase.execute(command);

        assertEquals(expectedResult, actual);
        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
    }

    @Test
    void givenAValidQuery_whenGatewayThrowsUnexpectedException_thenThrowsException() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway unexpected error";

        final var query = new SearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        when(castMemberGateway.findAll(query))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var command = ListCastMembersCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(castMemberGateway, times(1)).findAll(query);
    }

}
