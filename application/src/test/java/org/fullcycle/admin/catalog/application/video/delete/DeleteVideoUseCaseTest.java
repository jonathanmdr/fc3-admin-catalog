package org.fullcycle.admin.catalog.application.video.delete;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
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
import static org.mockito.Mockito.verify;

class DeleteVideoUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultDeleteVideoUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    void givenAValidId_whenCallsDeleteVideo_shouldDeleteIt() {
        final var expectedId = VideoID.unique();

        doNothing().when(videoGateway)
            .deleteById(any());

        assertDoesNotThrow(
            () -> this.useCase.execute(expectedId.getValue())
        );

        verify(videoGateway).deleteById(expectedId);
    }

    @Test
    void givenANotFoundId_whenCallsDeleteVideo_shouldBeOk() {
        final var notFoundIdentifier = IdentifierUtils.unique();
        final var expectedId = VideoID.from(notFoundIdentifier);

        doNothing().when(videoGateway)
            .deleteById(any());

        assertDoesNotThrow(
            () -> this.useCase.execute(expectedId.getValue())
        );

        verify(videoGateway).deleteById(expectedId);
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
    }

}
