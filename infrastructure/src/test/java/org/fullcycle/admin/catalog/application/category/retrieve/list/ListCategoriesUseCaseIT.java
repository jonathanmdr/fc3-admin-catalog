package org.fullcycle.admin.catalog.application.category.retrieve.list;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
class ListCategoriesUseCaseIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ListCategoriesUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @BeforeEach
    void mockUp() {
        final var categories = List.of(
            Category.newCategory("Filmes", null, true),
            Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
            Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon", true),
            Category.newCategory("Documentários", null, true),
            Category.newCategory("Sports", null, true),
            Category.newCategory("Kids", "Categoria para crianças", true),
            Category.newCategory("Series", null, true)
        ).stream()
            .map(CategoryJpaEntity::from)
            .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    void givenAValidTerm_whenTermDoesNotMatchesPrePersisted_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "ZzZzZzZzZz";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var command = ListCategoriesCommand.with(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actual = useCase.execute(command);

        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
    }

    @ParameterizedTest
    @CsvSource({
        "fil,0,10,1,1,Filmes",
        "net,0,10,1,1,Netflix Originals",
        "ZON,0,10,1,1,Amazon Originals",
        "KI,0,10,1,1,Kids",
        "crianças,0,10,1,1,Kids",
        "da Amazon,0,10,1,1,Amazon Originals",
    })
    void givenAValidTerm_whenCallsListCategories_shouldReturnCategoriesFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var command = ListCategoriesCommand.with(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actual = useCase.execute(command);

        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedCategoryName, actual.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "name,asc,0,10,7,7,Amazon Originals",
        "name,desc,0,10,7,7,Sports",
        "createdAt,asc,0,10,7,7,Filmes",
        "createdAt,desc,0,10,7,7,Series",
    })
    void givenAValidSortAndDirection_whenCallsListCategories_thenShouldReturnCategoriesOrdered(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedTerms = "";

        final var command = ListCategoriesCommand.with(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actual = useCase.execute(command);

        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedCategoryName, actual.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "0,2,2,7,Amazon Originals;Documentários",
        "1,2,2,7,Filmes,Kids",
        "2,2,2,7,Netflix Originals,Series",
        "3,2,1,7,Sports",
    })
    void givenAValidPage_whenCallsListCategories_shouldReturnCategoriesPaginated(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoriesName
    ) {
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var command = ListCategoriesCommand.with(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actual = useCase.execute(command);

        assertEquals(expectedItemsCount, actual.items().size());
        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());

        int index = 0;
        for (String expectedName : expectedCategoriesName.split(";")) {
            assertEquals(expectedName, actual.items().get(index).name());
            index++;
        }

    }

}
