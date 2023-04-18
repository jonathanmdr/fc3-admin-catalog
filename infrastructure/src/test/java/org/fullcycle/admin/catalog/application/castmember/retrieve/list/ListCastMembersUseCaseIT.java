package org.fullcycle.admin.catalog.application.castmember.retrieve.list;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class ListCastMembersUseCaseIT {

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private ListCastMembersUseCase useCase;

    @Test
    void givenAValidQuery_whenCallsListCastMembers_thenReturnCastMembers() {
        final var castMembers = List.of(
            CastMember.newMember("Vin Diesel", CastMemberType.ACTOR),
            CastMember.newMember("The Rock", CastMemberType.ACTOR)
        );

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAll(
            castMembers.stream()
                .map(CastMemberJpaEntity::from)
                .toList()
        );

        assertThat(castMemberRepository.count()).isEqualTo(2);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            castMembers.size(),
            castMembers
        );

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(ListCastMembersOutput::from);

        final var command = ListCastMembersCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var actual = useCase.execute(command);

        assertThat(actual).isEqualTo(expectedResult);
        assertThat(actual.items().size()).isEqualTo(expectedItemsCount);
        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(castMembers.size());
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

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedTotal,
            castMembers
        );

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(ListCastMembersOutput::from);

        final var command = ListCastMembersCommand.with(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        assertThat(castMemberRepository.count()).isZero();

        final var actual = useCase.execute(command);

        assertThat(actual).isEqualTo(expectedResult);
        assertThat(actual.items().size()).isEqualTo(expectedItemsCount);
        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(castMembers.size());
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

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(castMemberGateway).findAll(any());

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

        assertThat(actual.getMessage()).isEqualTo(expectedErrorMessage);
        verify(castMemberGateway, times(1)).findAll(query);
    }

}
