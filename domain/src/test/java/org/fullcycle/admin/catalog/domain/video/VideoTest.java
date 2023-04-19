package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(actual.getVideo().isEmpty());
        assertTrue(actual.getTrailer().isEmpty());
        assertTrue(actual.getBanner().isEmpty());
        assertTrue(actual.getThumbnail().isEmpty());
        assertTrue(actual.getThumbnailHalf().isEmpty());

        assertDoesNotThrow(
            () -> actual.validate(new ThrowsValidationHandler())
        );
    }

}
