package org.fullcycle.admin.catalog.application.video.media.upload;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoResource;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UploadMediaUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultUploadMediaUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(this.videoGateway, this.mediaResourceGateway);
    }

    @Test
    void givenCommandToUpload_whenIsValid_thenShouldUpdateVideoMediaPersisted() {
        final var video = Fixtures.VideoFixture.video();
        final var expectedVideoId = video.getId();
        final var expectedMediaType = MediaType.VIDEO;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedMediaType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedMediaType);
        final var expectedMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.mediaResourceGateway.storeAudioVideo(any(), any())).thenReturn(expectedMedia);
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);

        final var actual = this.useCase.execute(command);

        assertEquals(expectedMediaType, actual.mediaType());
        assertEquals(expectedVideoId.getValue(), actual.videoId());

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.mediaResourceGateway).storeAudioVideo(expectedVideoId, expectedVideoResource);
        verify(this.videoGateway).update(argThat(actualVideo -> {
            assertTrue(actualVideo.getVideo().isPresent());
            assertEquals(expectedMedia, actualVideo.getVideo().get());
            assertTrue(actualVideo.getTrailer().isEmpty());
            assertTrue(actualVideo.getBanner().isEmpty());
            assertTrue(actualVideo.getThumbnail().isEmpty());
            assertTrue(actualVideo.getThumbnailHalf().isEmpty());
            return true;
        }));
    }

    @Test
    void givenCommandToUpload_whenIsValid_thenShouldTrailerMediaPersisted() {
        final var video = Fixtures.VideoFixture.video();
        final var expectedVideoId = video.getId();
        final var expectedMediaType = MediaType.TRAILER;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedMediaType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedMediaType);
        final var expectedMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.mediaResourceGateway.storeAudioVideo(any(), any())).thenReturn(expectedMedia);
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);

        final var actual = this.useCase.execute(command);

        assertEquals(expectedMediaType, actual.mediaType());
        assertEquals(expectedVideoId.getValue(), actual.videoId());

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.mediaResourceGateway).storeAudioVideo(expectedVideoId, expectedVideoResource);
        verify(this.videoGateway).update(argThat(actualTrailer -> {
            assertTrue(actualTrailer.getVideo().isEmpty());
            assertTrue(actualTrailer.getTrailer().isPresent());
            assertEquals(expectedMedia, actualTrailer.getTrailer().get());
            assertTrue(actualTrailer.getBanner().isEmpty());
            assertTrue(actualTrailer.getThumbnail().isEmpty());
            assertTrue(actualTrailer.getThumbnailHalf().isEmpty());
            return true;
        }));
    }

    @Test
    void givenCommandToUpload_whenIsValid_thenShouldBannerMediaPersisted() {
        final var video = Fixtures.VideoFixture.video();
        final var expectedVideoId = video.getId();
        final var expectedMediaType = MediaType.BANNER;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedMediaType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedMediaType);
        final var expectedMedia = Fixtures.AudioVideoMediaFixture.image(expectedMediaType);

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.mediaResourceGateway.storeImage(any(), any())).thenReturn(expectedMedia);
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);

        final var actual = this.useCase.execute(command);

        assertEquals(expectedMediaType, actual.mediaType());
        assertEquals(expectedVideoId.getValue(), actual.videoId());

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.mediaResourceGateway).storeImage(expectedVideoId, expectedVideoResource);
        verify(this.videoGateway).update(argThat(actualBanner -> {
            assertTrue(actualBanner.getVideo().isEmpty());
            assertTrue(actualBanner.getTrailer().isEmpty());
            assertTrue(actualBanner.getBanner().isPresent());
            assertEquals(expectedMedia, actualBanner.getBanner().get());
            assertTrue(actualBanner.getThumbnail().isEmpty());
            assertTrue(actualBanner.getThumbnailHalf().isEmpty());
            return true;
        }));
    }

    @Test
    void givenCommandToUpload_whenIsValid_thenShouldThumbnailMediaPersisted() {
        final var video = Fixtures.VideoFixture.video();
        final var expectedVideoId = video.getId();
        final var expectedMediaType = MediaType.THUMBNAIL;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedMediaType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedMediaType);
        final var expectedMedia = Fixtures.AudioVideoMediaFixture.image(expectedMediaType);

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.mediaResourceGateway.storeImage(any(), any())).thenReturn(expectedMedia);
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);

        final var actual = this.useCase.execute(command);

        assertEquals(expectedMediaType, actual.mediaType());
        assertEquals(expectedVideoId.getValue(), actual.videoId());

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.mediaResourceGateway).storeImage(expectedVideoId, expectedVideoResource);
        verify(this.videoGateway).update(argThat(actualThumbnail -> {
            assertTrue(actualThumbnail.getVideo().isEmpty());
            assertTrue(actualThumbnail.getTrailer().isEmpty());
            assertTrue(actualThumbnail.getBanner().isEmpty());
            assertTrue(actualThumbnail.getThumbnail().isPresent());
            assertEquals(expectedMedia, actualThumbnail.getThumbnail().get());
            assertTrue(actualThumbnail.getThumbnailHalf().isEmpty());
            return true;
        }));
    }

    @Test
    void givenCommandToUpload_whenIsValid_thenShouldThumbnailHalfMediaPersisted() {
        final var video = Fixtures.VideoFixture.video();
        final var expectedVideoId = video.getId();
        final var expectedMediaType = MediaType.THUMBNAIL_HALF;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedMediaType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedMediaType);
        final var expectedMedia = Fixtures.AudioVideoMediaFixture.image(expectedMediaType);

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.mediaResourceGateway.storeImage(any(), any())).thenReturn(expectedMedia);
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);

        final var actual = this.useCase.execute(command);

        assertEquals(expectedMediaType, actual.mediaType());
        assertEquals(expectedVideoId.getValue(), actual.videoId());

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.mediaResourceGateway).storeImage(expectedVideoId, expectedVideoResource);
        verify(this.videoGateway).update(argThat(actualThumbnailHalf -> {
            assertTrue(actualThumbnailHalf.getVideo().isEmpty());
            assertTrue(actualThumbnailHalf.getTrailer().isEmpty());
            assertTrue(actualThumbnailHalf.getBanner().isEmpty());
            assertTrue(actualThumbnailHalf.getThumbnail().isEmpty());
            assertTrue(actualThumbnailHalf.getThumbnailHalf().isPresent());
            assertEquals(expectedMedia, actualThumbnailHalf.getThumbnailHalf().get());
            return true;
        }));
    }

    @Test
    void givenCommandToUpload_whenIsInvalid_thenReturnNotFound() {
        final var video = Fixtures.VideoFixture.video();
        final var expectedVideoId = video.getId();
        final var expectedMediaType = MediaType.BANNER;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedMediaType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedMediaType);
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedVideoId.getValue());

        when(this.videoGateway.findById(any())).thenReturn(Optional.empty());

        final var command = UploadMediaCommand.with(expectedVideoId.getValue(), expectedVideoResource);

        assertThrows(
            NotFoundException.class,
            () -> this.useCase.execute(command),
            expectedErrorMessage
        );

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.mediaResourceGateway, never()).storeImage(any(), any());
        verify(this.videoGateway, never()).update(any());
    }

}
