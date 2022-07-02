package org.fullcycle.admin.catalog.domain.genre;

import org.fullcycle.admin.catalog.domain.exception.NotificationValidationException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenreTest {

    @Test
    void givenValidParams_whenCallNewGenre_shouldInstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategoriesSize = 0;

        final var actual = Genre.newGenre(expectedName, expectedIsActive);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategoriesSize, actual.getCategories().size());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenInvalidNullName_whenCallNewGenreAndValidate_shouldReceiveAError() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actual = assertThrows(
            NotificationValidationException.class,
            () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenInvalidEmptyName_whenCallNewGenreAndValidate_shouldReceiveAError() {
        final String expectedName = " ";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actual = assertThrows(
            NotificationValidationException.class,
            () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNameWithLengthGreaterThan255_whenCallNewGenreAndValidate_shouldReceiveAError() {
        final var leftLimit = 97;
        final var limitRight = 122;
        final var targetStringLength = 256;
        final String expectedName = new Random().ints(leftLimit, limitRight + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 characters";

        final var actual = assertThrows(
            NotificationValidationException.class,
            () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

}
