package org.fullcycle.admin.catalog.application.genre.delete;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class DeleteGenreUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway gateway;

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre() {
        final var genre = Genre.newGenre("Action", true);
        final var expectedId = genre.getId();

        doNothing().when(gateway).deleteById(any());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        verify(gateway, atMostOnce()).deleteById(expectedId);
    }

    @Test
    void givenAnInvalidGenreId_whenCallsDeleteGenre_shouldBeOk() {
        final var expectedId = GenreID.unique();

        doNothing().when(gateway).deleteById(any());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        verify(gateway, atMostOnce()).deleteById(expectedId);
    }

    @Test
    void givenAValidGenreId_whenCallsDeleteGenreAndGatewayThrowsUnexpectedException_shouldReceiveException() {
        final var genre = Genre.newGenre("Action", true);
        final var expectedId = genre.getId();

        doThrow(new IllegalStateException("Gateway error")).when(gateway).deleteById(any());

        assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(expectedId.getValue()),
            "Gateway error"
        );
        verify(gateway, atMostOnce()).deleteById(expectedId);
    }

}
