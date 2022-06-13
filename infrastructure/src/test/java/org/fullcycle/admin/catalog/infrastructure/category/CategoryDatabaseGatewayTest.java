package org.fullcycle.admin.catalog.infrastructure.category;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.infrastructure.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        assertEquals(null, categorySaved.getDescription());
        assertEquals(true, categorySaved.isActive());

        assertEquals(1, categoryRepository.count());

        final var categoryToUpdate = Category.with(category).update(expectedName, expectedDescription, expectedIsActive);
        final var actual = categoryDatabaseGateway.update(categoryToUpdate);

        assertEquals(1, categoryRepository.count());
        assertEquals(category.getId(), actual.getId());
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

}