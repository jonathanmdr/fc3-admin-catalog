package org.fullcycle.admin.catalog.infrastructure.category;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.infrastructure.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

}