package org.fullcycle.admin.catalog.application.category.retrieve.list;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCategoriesUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @BeforeEach
    void cleanup() {
        reset(categoryGateway);
    }

    @Test
    void givenAValidQuery_whenCallListCategories_thenReturnCategories() {
        final var categories = List.of(
            Category.newCategory("Filmes", null, true),
            Category.newCategory("Séries", null, true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query = new CategorySearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            categories.size(),
            categories
        );

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(ListCategoryOutput::from);

        when(categoryGateway.findAll(query))
            .thenReturn(expectedPagination);

        final var command = ListCategoriesCommand.with(
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
        assertEquals(categories.size(), actual.total());
    }

    @Test
    void givenAValidQuery_whenHasNoResults_thenReturnEmptyCategories() {
        final var categories = Collections.<Category>emptyList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query = new CategorySearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            categories.size(),
            categories
        );

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(ListCategoryOutput::from);

        when(categoryGateway.findAll(query))
            .thenReturn(expectedPagination);

        final var command = ListCategoriesCommand.with(
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
        assertEquals(categories.size(), actual.total());
    }

    @Test
    void givenAValidQuery_whenGatewayThrowsUnexpectedException_thenThrowsException() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway unexpected error";

        final var query = new CategorySearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection
        );

        when(categoryGateway.findAll(query))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var command = ListCategoriesCommand.with(
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
        verify(categoryGateway, times(1)).findAll(query);
    }

}
