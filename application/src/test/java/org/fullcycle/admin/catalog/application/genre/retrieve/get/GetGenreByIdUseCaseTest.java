package org.fullcycle.admin.catalog.application.genre.retrieve.get;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetGenreByIdUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway gateway;

    @InjectMocks
    private DefaultGetGenreByIdUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    void givenAValidGenreId_whenCallsGetGenreById_shouldReturnsGenre() {
        final var expected = Genre.newGenre("Action", true);
        final var expectedId = expected.getId();
        final var expectedFirstCategoryId = CategoryID.unique();
        final var expectedSecondCategoryId = CategoryID.unique();
        final var categories = List.of(
            expectedFirstCategoryId,
            expectedSecondCategoryId
        );
        expected.addCategories(categories);

        when(gateway.findById(expectedId)).thenReturn(Optional.of(expected));

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = useCase.execute(command);

        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getName(), actual.name());
        assertEquals(expected.isActive(), actual.isActive());
        assertEquals(expected.getCategories(), actual.categories());
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getUpdatedAt(), actual.updatedAt());
        assertEquals(expected.getDeletedAt(), actual.deletedAt());

        verify(gateway, atMostOnce()).findById(expectedId);
    }

    @Test
    void givenAnInvalidGenreId_whenCallsGetGenreById_shouldReturnsNotFoundException() {
        final var expectedId = GenreID.unique();
        final var expectedErrorMessage = "Genre with ID %s was not found".formatted(expectedId.getValue());

        when(gateway.findById(expectedId)).thenReturn(Optional.empty());

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(gateway, atMostOnce()).findById(expectedId);
    }

    @Test
    void givenAValidGenreId_whenCallsGetGenreByIdAndGatewayThrowsUnexpectedException_shouldReturnsException() {
        final var expected = Genre.newGenre("Action", true);
        final var expectedId = expected.getId();
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(gateway.findById(any()));

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(gateway, atMostOnce()).findById(expectedId);
    }

}
