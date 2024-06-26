package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VideoTest {

    @Test
    void givenValidParams_whenCallsNewVideo_shouldInstantiate() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actual = Video.newVideo(
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

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertEquals(actual.getCreatedAt(), actual.getUpdatedAt());
        assertTrue(actual.getVideo().isEmpty());
        assertTrue(actual.getTrailer().isEmpty());
        assertTrue(actual.getBanner().isEmpty());
        assertTrue(actual.getThumbnail().isEmpty());
        assertTrue(actual.getThumbnailHalf().isEmpty());
        assertTrue(actual.getDomainEvents().isEmpty());

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenAValidVideo_whenCallsUpdate_shouldReturnUpdated() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());
        final var expectedEvent = VideoMediaCreated.with("id", "file");

        final var video = Video.newVideo(
            "Bla",
            "Foo",
            Year.of(1940),
            0.0,
            Rating.ER,
            true,
            true,
            Set.of(),
            Set.of(),
            Set.of()
        );
        video.registerEvent(expectedEvent);

        final var actual = Video.with(video).update(
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

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(video.getUpdatedAt().isBefore(actual.getUpdatedAt()));
        assertTrue(actual.getVideo().isEmpty());
        assertTrue(actual.getTrailer().isEmpty());
        assertTrue(actual.getBanner().isEmpty());
        assertTrue(actual.getThumbnail().isEmpty());
        assertTrue(actual.getThumbnailHalf().isEmpty());
        assertTrue(actual.getDomainEvents().contains(expectedEvent));

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenValidVideo_whenCallsUpdateBannerMedia_shouldUpdateBanner() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actual = Video.newVideo(
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

        final var expectedImageMedia = ImageMedia.newImageMedia("banner.jpeg", "a1b2c3", "/temp/banners");
        actual.updateBannerMedia(expectedImageMedia);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(actual.getCreatedAt().isBefore(actual.getUpdatedAt()));
        assertTrue(actual.getVideo().isEmpty());
        assertTrue(actual.getTrailer().isEmpty());
        assertFalse(actual.getBanner().isEmpty());
        assertEquals(expectedImageMedia, actual.getBanner().orElseThrow(() -> new IllegalStateException("Expected banner cannot be null or empty")));
        assertTrue(actual.getThumbnail().isEmpty());
        assertTrue(actual.getThumbnailHalf().isEmpty());

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenValidVideo_whenCallsUpdateThumbnailMedia_shouldUpdateThumbnail() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actual = Video.newVideo(
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

        final var expectedImageMedia = ImageMedia.newImageMedia("thumbnail.jpeg", "a1b2c3", "/temp/thumbnails");
        actual.updateThumbnailMedia(expectedImageMedia);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(actual.getCreatedAt().isBefore(actual.getUpdatedAt()));
        assertTrue(actual.getVideo().isEmpty());
        assertTrue(actual.getTrailer().isEmpty());
        assertTrue(actual.getBanner().isEmpty());
        assertFalse(actual.getThumbnail().isEmpty());
        assertEquals(expectedImageMedia, actual.getThumbnail().orElseThrow(() -> new IllegalStateException("Expected thumbnail cannot be null or empty")));
        assertTrue(actual.getThumbnailHalf().isEmpty());

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenValidVideo_whenCallsUpdateThumbnailHalfMedia_shouldUpdateThumbnailHalf() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actual = Video.newVideo(
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

        final var expectedImageMedia = ImageMedia.newImageMedia("thumbnail-half.jpeg", "a1b2c3", "/temp/thumbnails");
        actual.updateThumbnailHalfMedia(expectedImageMedia);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(actual.getCreatedAt().isBefore(actual.getUpdatedAt()));
        assertTrue(actual.getVideo().isEmpty());
        assertTrue(actual.getTrailer().isEmpty());
        assertTrue(actual.getBanner().isEmpty());
        assertTrue(actual.getThumbnail().isEmpty());
        assertFalse(actual.getThumbnailHalf().isEmpty());
        assertEquals(expectedImageMedia, actual.getThumbnailHalf().orElseThrow(() -> new IllegalStateException("Expected thumbnailHalf cannot be null or empty")));

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenValidVideo_whenCallsUpdateTrailerMedia_shouldUpdateTrailer() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actual = Video.newVideo(
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

        final var expectedAudioVideoMedia = AudioVideoMedia.newAudioVideoMedia(
            "trailer.mp4",
            "a1b2c3",
            "/temp/videos",
            "/temp/videos",
            MediaStatus.PENDING
        );
        actual.updateTrailerMedia(expectedAudioVideoMedia);

        final var expectedEvent = VideoMediaCreated.with(actual.getId().getValue(), expectedAudioVideoMedia.rawLocation());

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(actual.getCreatedAt().isBefore(actual.getUpdatedAt()));
        assertTrue(actual.getVideo().isEmpty());
        assertFalse(actual.getTrailer().isEmpty());
        assertEquals(expectedAudioVideoMedia, actual.getTrailer().orElseThrow(() -> new IllegalStateException("Expected trailer cannot be null or empty")));
        assertTrue(actual.getBanner().isEmpty());
        assertTrue(actual.getThumbnail().isEmpty());
        assertTrue(actual.getThumbnailHalf().isEmpty());
        assertEquals(1, actual.getDomainEvents().size());

        final var actualDomainEvent = (VideoMediaCreated) actual.getDomainEvents().getFirst();
        assertEquals(expectedEvent.resourceId(), actualDomainEvent.resourceId());
        assertEquals(expectedEvent.filePath(), actualDomainEvent.filePath());
        assertNotNull(actualDomainEvent.occurredOn());

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenValidVideo_whenCallsUpdateVideoMedia_shouldUpdateVideo() {
        final var expectedTitle = "System Design Interview";
        final var expectedDescription = """
            This is a simple System Design Interview with much insights into software development and architecture
            """;
        final var expectedLaunchedAt = Year.of(2023);
        final var expectedDuration = 120.10;
        final var expectedRating = Rating.L;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedCastMembers = Set.of(CastMemberID.unique());

        final var actual = Video.newVideo(
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

        final var expectedAudioVideoMedia = AudioVideoMedia.newAudioVideoMedia(
            "video.mp4",
            "a1b2c3",
            "/temp/videos",
            "/temp/videos",
            MediaStatus.PENDING
        );
        actual.updateVideoMedia(expectedAudioVideoMedia);

        final var expectedEvent = VideoMediaCreated.with(actual.getId().getValue(), expectedAudioVideoMedia.rawLocation());

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expectedTitle, actual.getTitle());
        assertEquals(expectedDescription, actual.getDescription());
        assertEquals(expectedLaunchedAt, actual.getLaunchedAt());
        assertEquals(expectedDuration, actual.getDuration());
        assertEquals(expectedOpened, actual.isOpened());
        assertEquals(expectedPublished, actual.isPublished());
        assertEquals(expectedRating, actual.getRating());
        assertEquals(expectedCategories, actual.getCategories());
        assertEquals(expectedGenres, actual.getGenres());
        assertEquals(expectedCastMembers, actual.getCastMembers());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        assertTrue(actual.getCreatedAt().isBefore(actual.getUpdatedAt()));
        assertFalse(actual.getVideo().isEmpty());
        assertEquals(expectedAudioVideoMedia, actual.getVideo().orElseThrow(() -> new IllegalStateException("Expected video cannot be null or empty")));
        assertTrue(actual.getTrailer().isEmpty());
        assertTrue(actual.getBanner().isEmpty());
        assertTrue(actual.getThumbnail().isEmpty());
        assertTrue(actual.getThumbnailHalf().isEmpty());
        assertEquals(1, actual.getDomainEvents().size());

        final var actualDomainEvent = (VideoMediaCreated) actual.getDomainEvents().getFirst();
        assertEquals(expectedEvent.resourceId(), actualDomainEvent.resourceId());
        assertEquals(expectedEvent.filePath(), actualDomainEvent.filePath());
        assertNotNull(actualDomainEvent.occurredOn());

        assertDoesNotThrow(
            () -> actual.validate(ThrowsValidationHandler.create())
        );
    }

    @Test
    void givenValidParams_whenCallsWith_shouldCreateWithoutEvents() {
        final var actual = Video.with(
            VideoID.unique(),
            "System Design Interview",
            "This is a simple System Design Interview with much insights into software development and architecture",
            Year.of(2023),
            120.10,
            Rating.L,
            false,
            false,
            Instant.now(),
            Instant.now(),
            null,
            null,
            null,
            null,
            null,
            Set.of(CategoryID.unique()),
            Set.of(GenreID.unique()),
            Set.of(CastMemberID.unique())
        );

        assertNotNull(actual.getDomainEvents());
        assertTrue(actual.getDomainEvents().isEmpty());
    }

}
