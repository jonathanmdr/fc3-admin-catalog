package org.fullcycle.admin.catalog.infrastructure.category;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseGatewayIntegrationTest
class CategoryDatabaseGatewayTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDatabaseGateway categoryDatabaseGateway;

    @Test
    void givenAValidCategory_whenCallCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actual = categoryDatabaseGateway.create(category);

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(category.getCreatedAt(), actual.getCreatedAt());
        assertEquals(category.getUpdatedAt(), actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());

        final var actualEntity = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(actual.getId().getValue(), actualEntity.getId());
        assertEquals(expectedName, actualEntity.getName());
        assertEquals(expectedDescription, actualEntity.getDescription());
        assertEquals(expectedIsActive, actualEntity.isActive());
        assertEquals(category.getCreatedAt(), actualEntity.getCreatedAt());
        assertEquals(category.getUpdatedAt(), actualEntity.getUpdatedAt());
        assertNull(actualEntity.getDeletedAt());
    }

    @Test
    void givenAValidCategory_whenCallUpdate_shouldReturnUpdatedCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Bla", null, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var categorySaved = categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));
        assertEquals("Bla", categorySaved.getName());
        assertNull(categorySaved.getDescription());
        assertTrue(categorySaved.isActive());

        assertEquals(1, categoryRepository.count());

        final var categoryToUpdate = Category.with(category).update(expectedName, expectedDescription, expectedIsActive);
        final var actual = categoryDatabaseGateway.update(categoryToUpdate);

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId().getValue(), actual.getId().getValue());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(category.getCreatedAt(), actual.getCreatedAt());
        assertTrue(category.getUpdatedAt().isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());

        final var actualEntity = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(actual.getId().getValue(), actualEntity.getId());
        assertEquals(expectedName, actualEntity.getName());
        assertEquals(expectedDescription, actualEntity.getDescription());
        assertEquals(expectedIsActive, actualEntity.isActive());
        assertEquals(category.getCreatedAt(), actualEntity.getCreatedAt());
        assertTrue(category.getUpdatedAt().isBefore(actualEntity.getUpdatedAt()));
        assertNull(actualEntity.getDeletedAt());
    }

    @Test
    void givenAPrePersistedCategory_whenTryToDelete_shouldBeDeleteCategory() {
        final var category = Category.newCategory("Filmes", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        categoryDatabaseGateway.deleteById(category.getId());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenACategoryWithInvalidId_whenTryToDelete_shouldDoNothing() {
        assertEquals(0, categoryRepository.count());

        categoryDatabaseGateway.deleteById(CategoryID.unique());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void givenAValidCategoryId_whenCallFindById_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var actual = categoryDatabaseGateway.findById(category.getId()).get();

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId().getValue(), actual.getId().getValue());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(category.getCreatedAt(), actual.getCreatedAt());
        assertEquals(category.getUpdatedAt(), actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidCategoryId_whenCallFindById_shouldReturnEmpty() {
        final var category = Category.newCategory("Bla", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var actual = categoryDatabaseGateway.findById(CategoryID.unique());

        assertEquals(1, categoryRepository.count());
        assertTrue(actual.isEmpty());
    }

    @Test
    void givenPrePersistedCategories_whenCallFindAll_shouldReturnPaginatedCategories() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentarios", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedPerPage, actual.items().size());
        assertEquals(documentarios.getId().getValue(), actual.items().get(0).getId().getValue());
    }

    @Test
    void givenEmptyCategoryTable_whenCallFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertEquals(0, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedTotal, actual.items().size());
    }

    @Test
    void givenFollowPagination_whenCallFindAllWithSecondPage_shouldReturnCategoriesSecondPage() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentarios", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        var query = new SearchQuery(0, 1, "", "name", "asc");
        var actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedPerPage, actual.items().size());
        assertEquals(documentarios.getId().getValue(), actual.items().get(0).getId().getValue());

        expectedPage = 1;
        query = new SearchQuery(1, 1, "", "name", "asc");
        actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedPerPage, actual.items().size());
        assertEquals(filmes.getId().getValue(), actual.items().get(0).getId().getValue());

        expectedPage = 2;
        query = new SearchQuery(2, 1, "", "name", "asc");
        actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedPerPage, actual.items().size());
        assertEquals(series.getId().getValue(), actual.items().get(0).getId().getValue());
    }

    @Test
    void givenPrePersistedCategories_whenCallFindAllWithTermsMatchesName_shouldReturnPaginatedCategories() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentarios", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "doc", "name", "asc");
        final var actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedPerPage, actual.items().size());
        assertEquals(documentarios.getId().getValue(), actual.items().get(0).getId().getValue());
    }

    @Test
    void givenPrePersistedCategories_whenCallFindAllWithTermsMatchesDescription_shouldReturnPaginatedCategories() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria assistida", true);
        final var documentarios = Category.newCategory("Documentarios", "A categoria menos assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new SearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
        final var actual = categoryDatabaseGateway.findAll(query);

        assertEquals(expectedPage, actual.currentPage());
        assertEquals(expectedPerPage, actual.perPage());
        assertEquals(expectedTotal, actual.total());
        assertEquals(expectedPerPage, actual.items().size());
        assertEquals(filmes.getId().getValue(), actual.items().get(0).getId().getValue());
    }

}