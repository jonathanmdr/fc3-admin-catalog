package org.fullcycle.admin.catalog.application.video.retrieve.get;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetVideoByIdUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultGetVideoByIdUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of();
    }

    @Test
    void givenAValidId_whenCallsGetVideoById_shouldReturnIt() {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(Fixtures.CastMemberFixture.wesley().getId());
        final var expectedVideo = audioVideoMedia(MediaType.VIDEO);
        final var expectedTrailer = audioVideoMedia(MediaType.TRAILER);
        final var expectedBanner = imageMedia(MediaType.BANNER);
        final var expectedThumbnail = imageMedia(MediaType.THUMBNAIL);
        final var expectedThumbnailHalf = imageMedia(MediaType.THUMBNAIL_HALF);

        final var video = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedCastMembers
        );
        video.updateVideoMedia(expectedVideo);
        video.updateTrailerMedia(expectedTrailer);
        video.updateBannerMedia(expectedBanner);
        video.updateThumbnailMedia(expectedThumbnail);
        video.updateThumbnailHalfMedia(expectedThumbnailHalf);

        final var expectedId = video.getId();

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(video)));

        final var command = GetVideoByIdCommand.with(expectedId.getValue());

        final var actual = this.useCase.execute(command);

        assertNotNull(actual);
        assertEquals(expectedId.getValue(), actual.id());
        assertEquals(expectedTitle, actual.title());
        assertEquals(expectedDescription, actual.description());
        assertEquals(expectedLaunchedAt.getValue(), actual.launchedAt());
        assertEquals(expectedDuration, actual.duration());
        assertEquals(expectedOpened, actual.opened());
        assertEquals(expectedPublished, actual.published());
        assertEquals(expectedRating.label(), actual.rating());
        assertEquals(listIdAsString(expectedCategories), actual.categories());
        assertEquals(listIdAsString(expectedGenres), actual.genres());
        assertEquals(listIdAsString(expectedCastMembers), actual.castMembers());
        assertTrue(actual.video().isPresent());
        assertTrue(actual.trailer().isPresent());
        assertTrue(actual.banner().isPresent());
        assertTrue(actual.thumbnail().isPresent());
        assertTrue(actual.thumbnailHalf().isPresent());
        assertEquals(expectedVideo, actual.video().get());
        assertEquals(expectedTrailer, actual.trailer().get());
        assertEquals(expectedBanner, actual.banner().get());
        assertEquals(expectedThumbnail, actual.thumbnail().get());
        assertEquals(expectedThumbnailHalf, actual.thumbnailHalf().get());
        assertNotNull(actual.createdAt());
        assertNotNull(actual.updatedAt());
        assertEquals(video.getCreatedAt(), actual.createdAt());
        assertEquals(video.getUpdatedAt(), actual.updatedAt());

        verify(videoGateway).findById(expectedId);
    }

    @Test
    void givenAnInvalidVideoId_whenCallsGetVideoById_shouldReturnsNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        when(videoGateway.findById(any()))
            .thenReturn(Optional.empty());

        final var command = GetVideoByIdCommand.with(expectedId.getValue());
        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(videoGateway).findById(expectedId);
    }

    @Test
    void givenAValidVideoId_whenCallsGetVideoByIdAndGatewayThrowsUnexpectedException_shouldReturnsException() {
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Unexpected gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(videoGateway)
            .findById(any());

        final var command = GetVideoByIdCommand.with(expectedId.getValue());
        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(videoGateway, atMostOnce()).findById(expectedId);
    }

    private AudioVideoMedia audioVideoMedia(final MediaType type) {
        final var checksum = UUID.randomUUID().toString();
        return AudioVideoMedia.newAudioVideoMedia(
            type.name().toLowerCase(),
            checksum,
            "/videos/".concat(checksum),
            "",
            MediaStatus.PENDING
        );
    }

    private ImageMedia imageMedia(final MediaType type) {
        final var checksum = UUID.randomUUID().toString();
        return ImageMedia.newImageMedia(
            type.name().toLowerCase(),
            checksum,
            "/images/".concat(checksum)
        );
    }

}
