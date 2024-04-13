package org.fullcycle.admin.catalog.application.video.media.update;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateMediaStatusUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultUpdateMediaStatusUseCase subject;

    @Override
    protected List<Object> getMocks() {
        return List.of(this.videoGateway);
    }

    @Test
    void givenACommandForUpdateVideoMediaStatusToCompleted_whenIsValid_thenShouldBeUpdateStatusAndEncodedLocation() {
        final var expectedMediaStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFileName = "video.mp4";
        final var expectedMediaType = MediaType.VIDEO;
        final var expectedAudioVideoMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);
        final var video = Fixtures.VideoFixture.video().setVideo(expectedAudioVideoMedia);
        final var expectedVideoId = video.getId();

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UpdateMediaStatusCommand.with(
            expectedMediaStatus,
            expectedVideoId.getValue(),
            expectedAudioVideoMedia.id(),
            expectedFolder,
            expectedFileName
        );
        this.subject.execute(command);

        verify(this.videoGateway).findById(expectedVideoId);

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(this.videoGateway).update(captor.capture());

        final var actualVideo = captor.getValue();
        assertTrue(actualVideo.getTrailer().isEmpty());
        assertTrue(actualVideo.getVideo().isPresent());

        final var actualAudioVideoMedia = actualVideo.getVideo().get();

        assertEquals(expectedAudioVideoMedia.id(), actualAudioVideoMedia.id());
        assertEquals(expectedMediaStatus, actualAudioVideoMedia.status());
        assertEquals(expectedAudioVideoMedia.checksum(), actualAudioVideoMedia.checksum());
        assertEquals(expectedFolder.concat("/").concat(expectedFileName), actualAudioVideoMedia.encodedLocation());
        assertEquals(expectedAudioVideoMedia.rawLocation(), actualAudioVideoMedia.rawLocation());
    }

    @Test
    void givenACommandForUpdateVideoMediaStatusToProcessing_whenIsValid_thenShouldBeUpdateStatusAndEncodedLocation() {
        final var expectedMediaStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFileName = null;
        final var expectedMediaType = MediaType.VIDEO;
        final var expectedAudioVideoMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);
        final var video = Fixtures.VideoFixture.video().setVideo(expectedAudioVideoMedia);
        final var expectedVideoId = video.getId();

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UpdateMediaStatusCommand.with(
            expectedMediaStatus,
            expectedVideoId.getValue(),
            expectedAudioVideoMedia.id(),
            expectedFolder,
            expectedFileName
        );
        this.subject.execute(command);

        verify(this.videoGateway).findById(expectedVideoId);

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(this.videoGateway).update(captor.capture());

        final var actualVideo = captor.getValue();
        assertTrue(actualVideo.getTrailer().isEmpty());
        assertTrue(actualVideo.getVideo().isPresent());

        final var actualAudioVideoMedia = actualVideo.getVideo().get();

        assertEquals(expectedAudioVideoMedia.id(), actualAudioVideoMedia.id());
        assertEquals(expectedMediaStatus, actualAudioVideoMedia.status());
        assertEquals(expectedAudioVideoMedia.checksum(), actualAudioVideoMedia.checksum());
        assertTrue(actualAudioVideoMedia.encodedLocation().isBlank());
        assertEquals(expectedAudioVideoMedia.rawLocation(), actualAudioVideoMedia.rawLocation());
    }

    @Test
    void givenACommandForUpdateTrailerMediaStatusToCompleted_whenIsValid_thenShouldBeUpdateStatusAndEncodedLocation() {
        final var expectedMediaStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFileName = "video.mp4";
        final var expectedMediaType = MediaType.TRAILER;
        final var expectedAudioVideoMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);
        final var video = Fixtures.VideoFixture.video().setTrailer(expectedAudioVideoMedia);
        final var expectedVideoId = video.getId();

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UpdateMediaStatusCommand.with(
            expectedMediaStatus,
            expectedVideoId.getValue(),
            expectedAudioVideoMedia.id(),
            expectedFolder,
            expectedFileName
        );
        this.subject.execute(command);

        verify(this.videoGateway).findById(expectedVideoId);

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(this.videoGateway).update(captor.capture());

        final var actualVideo = captor.getValue();
        assertTrue(actualVideo.getTrailer().isPresent());
        assertTrue(actualVideo.getVideo().isEmpty());

        final var actualAudioVideoMedia = actualVideo.getTrailer().get();

        assertEquals(expectedAudioVideoMedia.id(), actualAudioVideoMedia.id());
        assertEquals(expectedMediaStatus, actualAudioVideoMedia.status());
        assertEquals(expectedAudioVideoMedia.checksum(), actualAudioVideoMedia.checksum());
        assertEquals(expectedFolder.concat("/").concat(expectedFileName), actualAudioVideoMedia.encodedLocation());
        assertEquals(expectedAudioVideoMedia.rawLocation(), actualAudioVideoMedia.rawLocation());
    }

    @Test
    void givenACommandForUpdateTrailerMediaStatusToProcessing_whenIsValid_thenShouldBeUpdateStatusAndEncodedLocation() {
        final var expectedMediaStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFileName = null;
        final var expectedMediaType = MediaType.TRAILER;
        final var expectedAudioVideoMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);
        final var video = Fixtures.VideoFixture.video().setTrailer(expectedAudioVideoMedia);
        final var expectedVideoId = video.getId();

        when(this.videoGateway.findById(any())).thenReturn(Optional.of(video));
        when(this.videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var command = UpdateMediaStatusCommand.with(
            expectedMediaStatus,
            expectedVideoId.getValue(),
            expectedAudioVideoMedia.id(),
            expectedFolder,
            expectedFileName
        );
        this.subject.execute(command);

        verify(this.videoGateway).findById(expectedVideoId);

        final var captor = ArgumentCaptor.forClass(Video.class);
        verify(this.videoGateway).update(captor.capture());

        final var actualVideo = captor.getValue();
        assertTrue(actualVideo.getTrailer().isPresent());
        assertTrue(actualVideo.getVideo().isEmpty());

        final var actualAudioVideoMedia = actualVideo.getTrailer().get();

        assertEquals(expectedAudioVideoMedia.id(), actualAudioVideoMedia.id());
        assertEquals(expectedMediaStatus, actualAudioVideoMedia.status());
        assertEquals(expectedAudioVideoMedia.checksum(), actualAudioVideoMedia.checksum());
        assertTrue(actualAudioVideoMedia.encodedLocation().isBlank());
        assertEquals(expectedAudioVideoMedia.rawLocation(), actualAudioVideoMedia.rawLocation());
    }

    @Test
    void givenACommandForUpdateMediaStatusToCompleted_whenIsInvalid_thenShouldBeDoNothing() {
        final var expectedMediaStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFileName = "video.mp4";
        final var expectedMediaType = MediaType.VIDEO;
        final var expectedAudioVideoMedia = Fixtures.AudioVideoMediaFixture.audioVideo(expectedMediaType);
        final var video = Fixtures.VideoFixture.video().setVideo(expectedAudioVideoMedia);
        final var expectedVideoId = video.getId();

        when(this.videoGateway.findById(any())).thenReturn(Optional.empty());

        final var command = UpdateMediaStatusCommand.with(
            expectedMediaStatus,
            expectedVideoId.getValue(),
            "invalidId",
            expectedFolder,
            expectedFileName
        );

        assertThrows(
            NotFoundException.class,
            () -> this.subject.execute(command),
            "Video with ID %s was not found".formatted(expectedVideoId.getValue())
        );

        verify(this.videoGateway).findById(expectedVideoId);
        verify(this.videoGateway, never()).update(any());
    }

}
