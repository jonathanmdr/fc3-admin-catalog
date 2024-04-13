package org.fullcycle.admin.catalog.application.video.delete;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class DeleteVideoUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultDeleteVideoUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, mediaResourceGateway);
    }

    @Test
    void givenAValidId_whenCallsDeleteVideo_shouldDeleteIt() {
        final var expectedId = VideoID.unique();

        doNothing().when(videoGateway).deleteById(any());
        doNothing().when(mediaResourceGateway).clearResources(any());

        assertDoesNotThrow(
            () -> this.useCase.execute(expectedId.getValue())
        );

        verify(videoGateway).deleteById(expectedId);
        verify(mediaResourceGateway).clearResources(expectedId);
    }

    @Test
    void givenANotFoundId_whenCallsDeleteVideo_shouldBeOk() {
        final var notFoundIdentifier = IdentifierUtils.unique();
        final var expectedId = VideoID.from(notFoundIdentifier);

        doNothing().when(videoGateway).deleteById(any());
        doNothing().when(mediaResourceGateway).clearResources(any());

        assertDoesNotThrow(
            () -> this.useCase.execute(expectedId.getValue())
        );

        verify(videoGateway).deleteById(expectedId);
        verify(mediaResourceGateway).clearResources(expectedId);
    }

    @Test
    void givenAValidVideoId_whenCallsDeleteVideoAndGatewayThrowsUnexpectedException_shouldReceiveException() {
        final var expectedId = VideoID.unique();

        doThrow(new IllegalStateException("Unexpected gateway error"))
            .when(videoGateway)
            .deleteById(any());

        assertThrows(
            IllegalStateException.class,
            () -> this.useCase.execute(expectedId.getValue()),
            "Unexpected gateway error"
        );

        verify(videoGateway).deleteById(expectedId);
        verify(mediaResourceGateway, never()).clearResources(any());
    }

    @Test
    void givenAValidVideoId_whenCallsDeleteVideoAndMediaResourceGatewayThrowsUnexpectedException_shouldReceiveException() {
        final var expectedId = VideoID.unique();

        doNothing().when(videoGateway).deleteById(any());
        doThrow(new IllegalStateException("Unexpected gateway error"))
            .when(mediaResourceGateway)
            .clearResources(any());

        assertThrows(
            IllegalStateException.class,
            () -> this.useCase.execute(expectedId.getValue()),
            "Unexpected gateway error"
        );

        verify(videoGateway).deleteById(expectedId);
        verify(mediaResourceGateway).clearResources(any());
    }

}
