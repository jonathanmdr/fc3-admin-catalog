package org.fullcycle.admin.catalog.domain.category;

import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTest {

    @Test
    void givenAValidParam_whenCallNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
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

    @Test
    void givenAInvalidEmptyName_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = " ";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException = assertThrows(
            DomainException.class,
            () -> actual.validate(new ThrowsValidationHandler())
        );
        assertEquals("'name' should not be empty", actualException.getErrors().get(0).message());
        assertEquals(1, actualException.getErrors().size());
    }

    @Test
    void givenAInvalidNameLengthLessThan3Characters_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final String expectedName = "Fi ";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException = assertThrows(
            DomainException.class,
            () -> actual.validate(new ThrowsValidationHandler())
        );
        assertEquals("'name' must be between 3 and 255 characters", actualException.getErrors().get(0).message());
        assertEquals(1, actualException.getErrors().size());
    }

    @Test
    void givenAInvalidNameLengthMoreThan255Characters_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        final var leftLimit = 97;
        final var limitRight = 122;
        final var targetStringLength = 256;
        final String expectedName = new Random().ints(leftLimit, limitRight + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualException = assertThrows(
            DomainException.class,
            () -> actual.validate(new ThrowsValidationHandler())
        );
        assertEquals("'name' must be between 3 and 255 characters", actualException.getErrors().get(0).message());
        assertEquals(1, actualException.getErrors().size());
    }

    @Test
    void givenAValidNullDescription_whenCallNewCategoryAndValidate_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final String expectedDescription = null;
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> actual.validate(new ThrowsValidationHandler()));
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidEmptyDescription_whenCallNewCategoryAndValidate_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = " ";
        final var expectedIsActive = true;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> actual.validate(new ThrowsValidationHandler()));
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidFalseIsActive_whenCallNewCategoryAndValidate_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = " ";
        final var expectedIsActive = false;

        final var actual = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> actual.validate(new ThrowsValidationHandler()));
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertNotNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidActiveCategory_whenCallDeactivate_thenReturnCategoryInactivated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, true);
        final var createdAt = category.getCreatedAt();
        final var updatedAt = category.getUpdatedAt();

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertTrue(category.isActive());
        assertNull(category.getDeletedAt());

        final var actual = category.deactivate();

        assertDoesNotThrow(() -> actual.validate(new ThrowsValidationHandler()));
        assertEquals(category.getId(), actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertEquals(actual.getCreatedAt(), createdAt);
        assertTrue(actual.getUpdatedAt().isAfter(updatedAt));
        assertNotNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidInactiveCategory_whenCallActivate_thenReturnCategoryActivated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, false);
        final var createdAt = category.getCreatedAt();
        final var updatedAt = category.getUpdatedAt();

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertFalse(category.isActive());
        assertNotNull(category.getDeletedAt());

        final var actual = category.activate();

        assertDoesNotThrow(() -> actual.validate(new ThrowsValidationHandler()));
        assertEquals(category.getId(), actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertNotNull(actual.getCreatedAt());
        assertEquals(actual.getCreatedAt(), createdAt);
        assertTrue(actual.getUpdatedAt().isAfter(updatedAt));
        assertNull(actual.getDeletedAt());
    }

}