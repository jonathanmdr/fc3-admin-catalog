package org.fullcycle.admin.catalog.application.genre.delete;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@IntegrationTest
class DeleteGenreUseCaseIT {

    @SpyBean
    private GenreGateway gateway;

    @Autowired
    private DeleteGenreUseCase useCase;

    @Test
    void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre() {
        final var genre = Genre.newGenre("Action", true);
        final var expectedId = genre.getId();

        gateway.create(genre);

        var actual = gateway.findById(genre.getId());
        assertThat(actual).isPresent();

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        actual = gateway.findById(genre.getId());
        assertThat(actual).isEmpty();

        verify(gateway, atMostOnce()).deleteById(expectedId);
    }

    @Test
    void givenAnInvalidGenreId_whenCallsDeleteGenre_shouldBeOk() {
        final var expectedId = GenreID.unique();

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
