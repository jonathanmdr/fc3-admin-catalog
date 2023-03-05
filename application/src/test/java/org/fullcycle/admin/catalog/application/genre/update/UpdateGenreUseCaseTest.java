package org.fullcycle.admin.catalog.application.genre.update;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateGenreUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultUpdateGenreUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("Bla", true);

        final var expectedGenreId = genre.getId();
        final var expectedName = "Ação";
        final var expectedActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            expectedName,
            expectedActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertEquals(expectedGenreId.getValue(), actual.id());

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(genreGateway, atLeastOnce()).update(argThat(updatedGenre ->
            Objects.equals(expectedGenreId, updatedGenre.getId())
            && Objects.equals(expectedName, updatedGenre.getName())
            && Objects.equals(expectedActive, updatedGenre.isActive())
            && Objects.equals(expectedCategories, updatedGenre.getCategories())
            && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
            && Objects.nonNull(updatedGenre.getUpdatedAt())
            && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
            && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("Ação", true);

        final var expectedGenreId = genre.getId();
        final var expectedName = "Ação";
        final var expectedActive = false;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            expectedName,
            expectedActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        assertTrue(genre.isActive());
        assertNull(genre.getDeletedAt());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertEquals(expectedGenreId.getValue(), actual.id());

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(genreGateway, atLeastOnce()).update(argThat(updatedGenre ->
            Objects.equals(expectedGenreId, updatedGenre.getId())
                && Objects.equals(expectedName, updatedGenre.getName())
                && Objects.equals(expectedActive, updatedGenre.isActive())
                && Objects.equals(expectedCategories, updatedGenre.getCategories())
                && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                && Objects.nonNull(updatedGenre.getUpdatedAt())
                && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                && Objects.nonNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("Bla", true);

        final var expectedGenreId = genre.getId();
        final var expectedName = "Ação";
        final var expectedActive = true;
        final var expectedCategoryOne = UUID.randomUUID();
        final var expectedCategoryTwo = UUID.randomUUID();
        final var expectedCategories = List.of(
            CategoryID.from(expectedCategoryOne),
            CategoryID.from(expectedCategoryTwo)
        );

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            expectedName,
            expectedActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertEquals(expectedGenreId.getValue(), actual.id());

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(categoryGateway, atLeastOnce()).existsByIds(eq(expectedCategories));
        verify(genreGateway, atLeastOnce()).update(argThat(updatedGenre ->
            Objects.equals(expectedGenreId, updatedGenre.getId())
                && Objects.equals(expectedName, updatedGenre.getName())
                && Objects.equals(expectedActive, updatedGenre.isActive())
                && Objects.equals(expectedCategories, updatedGenre.getCategories())
                && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                && Objects.nonNull(updatedGenre.getUpdatedAt())
                && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnNotificationException() {
        final var genre = Genre.newGenre("Bla", true);

        final var expectedGenreId = genre.getId();
        final var expectedActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            null,
            expectedActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        final var actual = assertThrows(
            DomainException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).update(any());
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotificationException() {
        final var genre = Genre.newGenre("Bla", true);

        final var expectedGenreId = genre.getId();
        final var expectedActive = true;
        final var expectedValidCategoryId = UUID.randomUUID().toString();
        final var expectedInvalidCategoryId = UUID.randomUUID().toString();
        final var expectedCategories = List.of(
            CategoryID.from(expectedValidCategoryId),
            CategoryID.from(expectedInvalidCategoryId)
        );
        final var expectedErrorCount = 2;
        final var expectedFirstErrorMessage = "Some categories could not be found: %s".formatted(expectedInvalidCategoryId);
        final var expectedSecondErrorMessage = "'name' should not be null";

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            null,
            expectedActive,
            asString(expectedCategories)
        );

        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(categoryGateway.existsByIds(any())).thenReturn(List.of(CategoryID.from(expectedValidCategoryId)));

        final var actual = assertThrows(
            DomainException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedFirstErrorMessage, actual.getErrors().get(0).message());
        assertEquals(expectedSecondErrorMessage, actual.getErrors().get(1).message());

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(categoryGateway, atLeastOnce()).existsByIds(eq(expectedCategories));
        verify(genreGateway, never()).update(any());
    }

    private List<String> asString(final List<CategoryID> categoryIDS) {
        return categoryIDS.stream()
            .map(CategoryID::getValue)
            .toList();
    }

}
