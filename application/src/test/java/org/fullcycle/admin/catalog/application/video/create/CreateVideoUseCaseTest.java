package org.fullcycle.admin.catalog.application.video.create;

import org.fullcycle.admin.catalog.application.Fixtures;
import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.Resource;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.fullcycle.admin.catalog.domain.video.Resource.Type;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateVideoUseCaseTest extends UseCaseTest {

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
    private DefaultCreateVideoUseCase useCase;

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
    void givenAValidCommand_whenCallsCreateVideo_shouldReturnVideoId () {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).create(argThat(video ->
                Objects.equals(expectedTitle, video.getTitle())
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
                && Objects.nonNull(video.getUpdatedAt())
        ));
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutCategories_whenCallsCreateVideo_shouldReturnVideoId() {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).create(argThat(video ->
            Objects.equals(expectedTitle, video.getTitle())
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
                && Objects.nonNull(video.getUpdatedAt())
        ));
        verify(categoryGateway, never()).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutGenres_whenCallsCreateVideo_shouldReturnVideoId () {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).create(argThat(video ->
            Objects.equals(expectedTitle, video.getTitle())
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
                && Objects.nonNull(video.getUpdatedAt())
        ));
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway, never()).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutCastMembers_whenCallsCreateVideo_shouldReturnVideoId () {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        mockAudioVideoMedia();
        mockImageMedia();
        when(videoGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).create(argThat(video ->
            Objects.equals(expectedTitle, video.getTitle())
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
                && Objects.nonNull(video.getUpdatedAt())
        ));
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway, never()).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, times(2)).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
    }

    @Test
    void givenAValidCommandWithoutResources_whenCallsCreateVideo_shouldReturnVideoId() {
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
        final Resource expectedVideo = null;
        final Resource expectedTrailer = null;
        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;

        final var command = CreateVideoCommand.with(
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

        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        when(videoGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).create(argThat(video ->
            Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
                && video.getVideo().isEmpty()
                && video.getTrailer().isEmpty()
                && video.getBanner().isEmpty()
                && video.getThumbnail().isEmpty()
                && video.getThumbnailHalf().isEmpty()
                && Objects.nonNull(video.getCreatedAt())
                && Objects.nonNull(video.getUpdatedAt())
        ));
        verify(categoryGateway).existsByIds(expectedCategories);
        verify(genreGateway).existsByIds(expectedGenres);
        verify(castMemberGateway).existsByIds(expectedCastMembers);
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
    }

    @Test
    void givenACommandWithNullTitle_whenCallsCreateVideo_shouldReturnNotificationException () {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not create aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway, never()).create(any());
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
    }

    @Test
    void givenACommandWithEmptyTitle_whenCallsCreateVideo_shouldReturnNotificationException () {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not create aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway, never()).create(any());
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
    }

    @Test
    void givenACommandWithTitleGreaterThan255Characters_whenCallsCreateVideo_shouldReturnNotificationException () {
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
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
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

        final var exception = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Could not create aggregate video"
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(videoGateway, never()).create(any());
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).existsByIds(any());
        verify(castMemberGateway, never()).existsByIds(any());
        verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
        verify(mediaResourceGateway, never()).storeImage(any(), any());
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any()))
            .thenAnswer(answer -> {
                final var resource = answer.getArgument(1, Resource.class);
                return AudioVideoMedia.with(
                    UUID.randomUUID().toString(),
                    resource.name(),
                    "/videos",
                    "",
                    MediaStatus.PENDING
                );
            });
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any()))
            .thenAnswer(answer -> {
                final var resource = answer.getArgument(1, Resource.class);
                return ImageMedia.with(
                    UUID.randomUUID().toString(),
                    resource.name(),
                    "/images"
                );
            });
    }

}
