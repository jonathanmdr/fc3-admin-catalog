package org.fullcycle.admin.catalog.domain.genre;

import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void givenInvalidNullName_whenCallNewGenre_shouldReceiveAnError() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actual = assertThrows(
            NotificationException.class,
            () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenInvalidEmptyName_whenCallNewGenre_shouldReceiveAnError() {
        final String expectedName = " ";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actual = assertThrows(
            NotificationException.class,
            () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNameWithLengthGreaterThan255_whenCallNewGenre_shouldReceiveAnError() {
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
            NotificationException.class,
            () -> Genre.newGenre(expectedName, expectedIsActive)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenAnActiveGenre_whenCallDeactivate_shouldReceiveOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategoriesSize = 0;

        final var actual = Genre.newGenre(expectedName, true);

        assertTrue(actual.isActive());
        assertNull(actual.getDeletedAt());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.deactivate();

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategoriesSize, actual.getCategories().size());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNotNull(actual.getDeletedAt());
    }

    @Test
    void givenAnInactiveGenre_whenCallActivate_shouldReceiveOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategoriesSize = 0;

        final var actual = Genre.newGenre(expectedName, false);

        assertFalse(actual.isActive());
        assertNotNull(actual.getDeletedAt());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.activate();

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategoriesSize, actual.getCategories().size());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidInactiveGenre_whenCallUpdateWithActivate_shouldReceiveGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.unique());

        final var actual = Genre.newGenre("acao", false);

        assertFalse(actual.isActive());
        assertNotNull(actual.getDeletedAt());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.update(expectedName, expectedIsActive, expectedCategories);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidActiveGenre_whenCallUpdateWithInactivate_shouldReceiveGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.of(CategoryID.unique());

        final var actual = Genre.newGenre("acao", true);

        assertTrue(actual.isActive());
        assertNull(actual.getDeletedAt());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.update(expectedName, expectedIsActive, expectedCategories);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNotNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidGenre_whenCallUpdateWithEmptyName_shouldReceiveAnError() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actual = assertThrows(
            NotificationException.class,
            () -> actualGenre.update(" ", false, Collections.emptyList())
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenAValidGenre_whenCallUpdateWithNullName_shouldReceiveAnError() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actual = assertThrows(
            NotificationException.class,
            () -> actualGenre.update(null, false, Collections.emptyList())
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenAValidGenre_whenCallUpdateWithInvalidNameWithLengthGreaterThan255_shouldReceiveAnError() {
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

        final var actualGenre = Genre.newGenre("Ação", expectedIsActive);

        final var actual = assertThrows(
            NotificationException.class,
            () -> actualGenre.update(expectedName, expectedIsActive, Collections.emptyList())
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());
    }

    @Test
    void givenAValidGenre_whenCallUpdateWithNullCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actualCreatedAt = actualGenre.getCreatedAt();
        final var actualUpdatedAt = actualGenre.getUpdatedAt();

        final var actual = assertDoesNotThrow(
            () -> actualGenre.update(expectedName, expectedIsActive, null)
        );

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidEmptyCategoriesGenre_whenCallAddCategory_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var expectedCategories = List.of(seriesId, moviesId);

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.addCategory(seriesId);
        actual.addCategory(moviesId);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidEmptyCategoriesGenre_whenCallAddCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var expectedCategories = List.of(seriesId, moviesId);

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.addCategories(expectedCategories);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithTwoCategories_whenCallRemoveCategory_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var expectedCategories = List.of(moviesId);

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        actual.update(expectedName, expectedIsActive, List.of(seriesId, moviesId));
        assertEquals(2, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.removeCategory(seriesId);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAValidGenreWithTwoCategories_whenCallRemoveCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var expectedCategories = List.of(moviesId);

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        actual.update(expectedName, expectedIsActive, List.of(seriesId, moviesId));
        assertEquals(2, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.removeCategories(List.of(seriesId));

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(actual.getUpdatedAt()));
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidNullAsCategoryID_whenCallAddCategory_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.addCategory(null);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertEquals(actualUpdatedAt, actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidNullAsCategoryID_whenCallAddCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.addCategories(null);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertEquals(actualUpdatedAt, actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidEmptyAsCategoryID_whenCallAddCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.addCategories(Collections.emptyList());

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertEquals(actualUpdatedAt, actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidNullAsCategoryID_whenCallRemoveCategory_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.removeCategory(null);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertEquals(actualUpdatedAt, actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidNullAsCategoryID_whenCallRemoveCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.removeCategories(null);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertEquals(actualUpdatedAt, actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

    @Test
    void givenAInvalidEmptyAsCategoryID_whenCallRemoveCategories_shouldReceiveOk() {
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = new ArrayList<CategoryID>();

        final var actual = Genre.newGenre(expectedName, expectedIsActive);
        assertEquals(0, actual.getCategories().size());

        final var actualCreatedAt = actual.getCreatedAt();
        final var actualUpdatedAt = actual.getUpdatedAt();

        actual.removeCategories(Collections.emptyList());

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(actualCreatedAt, actual.getCreatedAt());
        assertEquals(actualUpdatedAt, actual.getUpdatedAt());
        assertNull(actual.getDeletedAt());
    }

}
