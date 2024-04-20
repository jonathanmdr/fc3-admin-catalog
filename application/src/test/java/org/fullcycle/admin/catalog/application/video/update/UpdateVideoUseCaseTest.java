package org.fullcycle.admin.catalog.application.video.update;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.resource.Resource;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoResource;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateVideoUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultUpdateVideoUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            categoryGateway,
            genreGateway,
            castMemberGateway,
            mediaResourceGateway,
            videoGateway
        );
    }

    @Test
    void givenAValidCommand_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).update(argThat(video ->
                Objects.equals(expectedId, video.getId())
                && Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
                && video.getVideo().isPresent()
                && video.getTrailer().isPresent()
                && video.getBanner().isPresent()
                && video.getThumbnail().isPresent()
                && video.getThumbnailHalf().isPresent()
                && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
                && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
                && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
                && Objects.equals(expectedThumbnail.name(), video.getThumbnail().get().name())
                && Objects.equals(expectedThumbnailHalf.name(), video.getThumbnailHalf().get().name())
                && Objects.nonNull(video.getCreatedAt())
                && Objects.equals(createdVideo.getCreatedAt(), video.getCreatedAt())
                && video.getCreatedAt().isBefore(video.getUpdatedAt())
                && Objects.nonNull(video.getUpdatedAt())
                && createdVideo.getUpdatedAt().isBefore(video.getUpdatedAt())
        ));
        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutCategories_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).update(argThat(video ->
            Objects.equals(expectedId, video.getId())
                && Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
                && video.getVideo().isPresent()
                && video.getTrailer().isPresent()
                && video.getBanner().isPresent()
                && video.getThumbnail().isPresent()
                && video.getThumbnailHalf().isPresent()
                && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
                && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
                && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
                && Objects.equals(expectedThumbnail.name(), video.getThumbnail().get().name())
                && Objects.equals(expectedThumbnailHalf.name(), video.getThumbnailHalf().get().name())
                && Objects.nonNull(video.getCreatedAt())
                && Objects.equals(createdVideo.getCreatedAt(), video.getCreatedAt())
                && video.getCreatedAt().isBefore(video.getUpdatedAt())
                && Objects.nonNull(video.getUpdatedAt())
                && createdVideo.getUpdatedAt().isBefore(video.getUpdatedAt())
        ));
        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutGenres_whenCallsUpdateVideo_shouldReturnVideoId () {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).update(argThat(video ->
            Objects.equals(expectedId, video.getId())
                && Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
                && video.getVideo().isPresent()
                && video.getTrailer().isPresent()
                && video.getBanner().isPresent()
                && video.getThumbnail().isPresent()
                && video.getThumbnailHalf().isPresent()
                && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
                && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
                && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
                && Objects.equals(expectedThumbnail.name(), video.getThumbnail().get().name())
                && Objects.equals(expectedThumbnailHalf.name(), video.getThumbnailHalf().get().name())
                && Objects.nonNull(video.getCreatedAt())
                && Objects.equals(createdVideo.getCreatedAt(), video.getCreatedAt())
                && video.getCreatedAt().isBefore(video.getUpdatedAt())
                && Objects.nonNull(video.getUpdatedAt())
                && createdVideo.getUpdatedAt().isBefore(video.getUpdatedAt())
        ));
        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway, never()).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutCastMembers_whenCallsUpdateVideo_shouldReturnVideoId () {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).update(argThat(video ->
            Objects.equals(expectedId, video.getId())
                && Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
                && video.getVideo().isPresent()
                && video.getTrailer().isPresent()
                && video.getBanner().isPresent()
                && video.getThumbnail().isPresent()
                && video.getThumbnailHalf().isPresent()
                && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
                && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
                && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
                && Objects.equals(expectedThumbnail.name(), video.getThumbnail().get().name())
                && Objects.equals(expectedThumbnailHalf.name(), video.getThumbnailHalf().get().name())
                && Objects.nonNull(video.getCreatedAt())
                && Objects.equals(createdVideo.getCreatedAt(), video.getCreatedAt())
                && video.getCreatedAt().isBefore(video.getUpdatedAt())
                && Objects.nonNull(video.getUpdatedAt())
                && createdVideo.getUpdatedAt().isBefore(video.getUpdatedAt())
        ));
        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway, never()).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutResources_whenCallsUpdateVideo_shouldReturnVideoId() {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideoResource =null;
        final Resource expectedTrailerResource =null;
        final Resource expectedBannerResource =null;
        final Resource expectedThumbnailResource =null;
        final Resource expectedThumbnailHalfResource =null;
        final var expectedVideo = audioVideoMedia(MediaType.VIDEO);
        final var expectedTrailer = audioVideoMedia(MediaType.TRAILER);
        final var expectedBanner = imageMedia(MediaType.BANNER);
        final var expectedThumbnail = imageMedia(MediaType.THUMBNAIL);
        final var expectedThumbnailHalf = imageMedia(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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
        createdVideo.updateVideoMedia(expectedVideo);
        createdVideo.updateTrailerMedia(expectedTrailer);
        createdVideo.updateBannerMedia(expectedBanner);
        createdVideo.updateThumbnailMedia(expectedThumbnail);
        createdVideo.updateThumbnailHalfMedia(expectedThumbnailHalf);

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideoResource,
            expectedTrailerResource,
            expectedBannerResource,
            expectedThumbnailResource,
            expectedThumbnailHalfResource
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        when(videoGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).update(argThat(video ->
            Objects.equals(expectedId, video.getId())
                && Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
                && video.getVideo().isPresent()
                && video.getTrailer().isPresent()
                && video.getBanner().isPresent()
                && video.getThumbnail().isPresent()
                && video.getThumbnailHalf().isPresent()
                && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
                && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
                && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
                && Objects.equals(expectedThumbnail.name(), video.getThumbnail().get().name())
                && Objects.equals(expectedThumbnailHalf.name(), video.getThumbnailHalf().get().name())
                && Objects.nonNull(video.getCreatedAt())
                && Objects.equals(createdVideo.getCreatedAt(), video.getCreatedAt())
                && video.getCreatedAt().isBefore(video.getUpdatedAt())
                && Objects.nonNull(video.getUpdatedAt())
                && createdVideo.getUpdatedAt().isBefore(video.getUpdatedAt())
        ));
        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
    }

    @Test
    void givenACommandWithNullTitle_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'title' should not be null";
        final var expectedErrorCount = 1;

        final String expectedTitle = null;
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithEmptyTitle_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final String expectedTitle = "   ";
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithTitleGreaterThan255Characters_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'title' must be between 1 and 255 characters";
        final var expectedErrorCount = 1;

        final var leftLimit = 97;
        final var limitRight = 122;
        final var targetStringLength = 256;
        final String expectedTitle = new Random().ints(leftLimit, limitRight + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNullDescription_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'description' should not be null";
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final String expectedDescription = null;
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithEmptyDescription_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'description' should not be empty";
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final String expectedDescription = "   ";
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithDescriptionGreaterThan1000Characters_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'description' must be between 1 and 1000 characters";
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var leftLimit = 97;
        final var limitRight = 122;
        final var targetStringLength = 1001;
        final String expectedDescription = new Random().ints(leftLimit, limitRight + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
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

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNullLaunchedAt_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'launchedAt' should not be null";
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final Integer expectedLaunchedAt = null;
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            Year.of(Fixtures.VideoFixture.year()),
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedCastMembers
        );

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNullRating_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedErrorMessage = "'rating' should not be null";
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final String expectedRating = null;
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            Fixtures.VideoFixture.rating(),
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedCastMembers
        );

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating,
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNotFoundCategoryId_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedNotFoundCategoryId = Fixtures.CategoryFixture.classes().getId();
        final var expectedErrorMessage = "Some categories could not be found: %s".formatted(expectedNotFoundCategoryId.getValue());
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedNotFoundCategories = Set.of(expectedNotFoundCategoryId);
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            Fixtures.VideoFixture.rating(),
            expectedOpened,
            expectedPublished,
            expectedNotFoundCategories,
            expectedGenres,
            expectedCastMembers
        );

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedNotFoundCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(Collections.emptyList());
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedNotFoundCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNotFoundGenreId_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedNotFoundGenreId = Fixtures.GenreFixture.technology().getId();
        final var expectedErrorMessage = "Some genres could not be found: %s".formatted(expectedNotFoundGenreId.getValue());
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedNotFoundGenres = Set.of(expectedNotFoundGenreId);
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            Fixtures.VideoFixture.rating(),
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedNotFoundGenres,
            expectedCastMembers
        );

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedNotFoundGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(Collections.emptyList());
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedNotFoundGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNotFoundCastMemberId_whenCallsUpdateVideo_shouldReturnNotificationException() {
        final var expectedNotFoundFirstCastMember = Fixtures.CastMemberFixture.wesley().getId();
        final var expectedErrorMessage = "Some cast members could not be found: %s".formatted(expectedNotFoundFirstCastMember.getValue());
        final var expectedErrorCount = 1;

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedNotFoundCastMembers = Set.of(expectedNotFoundFirstCastMember);
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            Fixtures.VideoFixture.rating(),
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedNotFoundCastMembers
        );

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedNotFoundCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(Collections.emptyList());

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not update aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedNotFoundCastMembers);
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenACommandWithNotFoundVideoId_whenCallsUpdateVideo_shouldReturnNotFoundException() {
        final var expectedNotFoundId = VideoID.unique();
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedNotFoundId.getValue());

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var command = UpdateVideoCommand.with(
            expectedNotFoundId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.empty());

        final var exception = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(videoGateway).findById(expectedNotFoundId);
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
        verify(videoGateway, never()).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateVideoAndGatewayThrowsUnexpectedException_shouldReturnInternalErrorException() {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final Resource expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final Resource expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final Resource expectedThumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL_HALF);

        final var createdVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            Fixtures.VideoFixture.rating(),
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedCastMembers
        );

        final var expectedId = createdVideo.getId();

        final var command = UpdateVideoCommand.with(
            expectedId.getValue(),
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(videoGateway.findById(any()))
            .thenReturn(Optional.of(Video.with(createdVideo)));
        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.update(any()))
            .thenThrow(new IllegalStateException("Gateway unexpected error"));

        final var exception = assertThrows(
            InternalErrorException.class,
            () -> useCase.execute(command)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().startsWith("An error has occurred on updating a video with ID: "));

        verify(videoGateway).findById(expectedId);
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
        verify(videoGateway).update(any());
    }

    private AudioVideoMedia audioVideoMedia(final MediaType type) {
        final var checksum = UUID.randomUUID().toString();
        return AudioVideoMedia.newAudioVideoMedia(
            checksum,
            type.name().toLowerCase(),
            "/videos/".concat(checksum),
            "",
            MediaStatus.PENDING
        );
    }

    private ImageMedia imageMedia(final MediaType type) {
        final var checksum = UUID.randomUUID().toString();
        return ImageMedia.newImageMedia(
            checksum,
            type.name().toLowerCase(),
            "/images/".concat(checksum)
        );
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any()))
            .thenAnswer(answer -> {
                final var videoResource = answer.getArgument(1, VideoResource.class);
                return AudioVideoMedia.newAudioVideoMedia(
                    videoResource.resource().name(),
                    videoResource.resource().checksum(),
                    "/videos",
                    "",
                    MediaStatus.PENDING
                );
            });
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any()))
            .thenAnswer(answer -> {
                final var videoResource = answer.getArgument(1, VideoResource.class);
                return ImageMedia.newImageMedia(
                    videoResource.resource().name(),
                    videoResource.resource().checksum(),
                    "/images"
                );
            });
    }

}
