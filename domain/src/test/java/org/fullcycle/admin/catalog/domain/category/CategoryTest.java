package org.fullcycle.admin.catalog.domain.category;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    void givenAValidParam_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        Assertions.assertEquals(expectedName, actual.getName());
        Assertions.assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        Assertions.assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidNullName_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException = assertThrows(
            DomainException.class,
            () -> actual.validate(new ThrowsValidationHandler())
        );
        assertEquals("'name' should not be null", actualException.getErrors().get(0).message());
        assertEquals(1, actualException.getErrors().size());
    }

}