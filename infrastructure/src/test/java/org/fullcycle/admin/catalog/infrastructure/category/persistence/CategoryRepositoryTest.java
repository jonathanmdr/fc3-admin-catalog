package org.fullcycle.admin.catalog.infrastructure.category.persistence;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.DatabaseRepositoryIntegrationTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseRepositoryIntegrationTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenAnInvalidNullName_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "name";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.name; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.name";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var entity = CategoryJpaEntity.from(category);
        entity.setName(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.createdAt; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.createdAt";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var entity = CategoryJpaEntity.from(category);
        entity.setCreatedAt(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.updatedAt; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var entity = CategoryJpaEntity.from(category);
        entity.setUpdatedAt(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

}
