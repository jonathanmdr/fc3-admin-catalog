package org.fullcycle.admin.catalog.application.genre.create;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGenreUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any()))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(genreGateway, times(1))
            .create(argThat(genre ->
                Objects.nonNull(genre.getId())
                && Objects.equals(expectedName, genre.getName())
                && Objects.equals(expectedIsActive, genre.isActive())
                && Objects.equals(expectedCategories, genre.getCategories())
                && Objects.nonNull(genre.getCreatedAt())
                && Objects.nonNull(genre.getUpdatedAt())
                && Objects.isNull(genre.getDeletedAt())
            ));
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any()))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(genreGateway, times(1))
            .create(argThat(genre ->
                Objects.nonNull(genre.getId())
                && Objects.equals(expectedName, genre.getName())
                && Objects.equals(expectedIsActive, genre.isActive())
                && Objects.equals(expectedCategories, genre.getCategories())
                && Objects.nonNull(genre.getCreatedAt())
                && Objects.nonNull(genre.getUpdatedAt())
                && Objects.nonNull(genre.getDeletedAt())
            ));
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var expectedCategories = List.of(seriesId, moviesId);

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(categoryGateway.existsByIds(any()))
            .thenReturn(expectedCategories);

        when(genreGateway.create(any()))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(categoryGateway, times(1))
            .existsByIds(argThat(categoryIds ->
                Objects.equals(categoryIds, expectedCategories)
            ));

        verify(genreGateway, times(1))
            .create(argThat(genre ->
                Objects.nonNull(genre.getId())
                && Objects.equals(expectedName, genre.getName())
                && Objects.equals(expectedIsActive, genre.isActive())
                && Objects.equals(expectedCategories, genre.getCategories())
                && Objects.nonNull(genre.getCreatedAt())
                && Objects.nonNull(genre.getUpdatedAt())
                && Objects.isNull(genre.getDeletedAt())
            ));
    }

    @Test
    void givenAnInvalidEmptyName_whenCallsCreateGenre_shouldReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command)
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void givenAnInvalidNullName_whenCallsCreateGenre_shouldReturnDomainException() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command)
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var documentary = CategoryID.unique();
        final var expectedCategories = List.of(seriesId, moviesId, documentary);
        final var expectedErrorMessage = "Some categories could not be found: %s, %s".formatted(seriesId.getValue(), moviesId.getValue());
        final var expectedErrorCount = 1;

        when(categoryGateway.existsByIds(any()))
            .thenReturn(List.of(documentary));

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command)
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(categoryGateway, times(1)).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void givenAInvalidEmptyName_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var seriesId = CategoryID.unique();
        final var moviesId = CategoryID.unique();
        final var documentary = CategoryID.unique();
        final var expectedCategories = List.of(seriesId, moviesId, documentary);
        final var expectedErrorMessageOne = "Some categories could not be found: %s, %s".formatted(seriesId.getValue(), moviesId.getValue());
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

        when(categoryGateway.existsByIds(any()))
            .thenReturn(List.of(documentary));

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command)
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessageOne, actual.getErrors().get(0).message());
        assertEquals(expectedErrorMessageTwo, actual.getErrors().get(1).message());

        verify(categoryGateway, times(1)).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
            .map(CategoryID::getValue)
            .toList();
    }

}
