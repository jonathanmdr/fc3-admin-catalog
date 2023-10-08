package org.fullcycle.admin.catalog.application.video.media.get;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetMediaUseCaseTest extends UseCaseTest {

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultGetMediaUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway);
    }

    @Test
    void givenVideoIdAndType_whenValidCommand_thenReturnResource() {
        final var expectedId = VideoID.unique();
        final var expectedType = Fixtures.VideoFixture.mediaType();
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);

        when(this.mediaResourceGateway.getResource(expectedId, expectedType)).thenReturn(Optional.of(expectedResource));

        final var command = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        final var actual = this.useCase.execute(command);

        assertEquals(expectedResource.name(), actual.name());
        assertEquals(expectedResource.content(), actual.content());
        assertEquals(expectedResource.contentType(), actual.contentType());

        verify(this.mediaResourceGateway).getResource(expectedId, expectedType);
    }

    @Test
    void givenVideoIdAndType_whenIsNotFound_thenThrowsNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedType = Fixtures.VideoFixture.mediaType();

        when(this.mediaResourceGateway.getResource(expectedId, expectedType)).thenReturn(Optional.empty());

        final var command = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        assertThrows(
            NotFoundException.class,
            () -> this.useCase.execute(command),
            "Resource %s not found for video %s".formatted(expectedType.name(), expectedId.getValue())
        );

        verify(this.mediaResourceGateway).getResource(expectedId, expectedType);
    }

    @Test
    void givenVideoIdAndType_whenMediaTypeNotFound_thenThrowsNotFoundException() {
        final var expectedId = VideoID.unique();
        final var invalidMediaType = "INVALID_MEDIA_TYPE";

        final var command = GetMediaCommand.with(expectedId.getValue(), invalidMediaType);

        assertThrows(
            NotFoundException.class,
            () -> this.useCase.execute(command),
            "Media type %s doesn't exists".formatted(invalidMediaType)
        );

        verify(this.mediaResourceGateway, never()).getResource(any(), any());
    }

}
