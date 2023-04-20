package org.fullcycle.admin.catalog.domain.video;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AudioVideoMediaTest {

    @Test
    void givenValidParams_whenCreateANewVideo_shouldInstantiate() {
        final var expectedChecksum = "a1s2d3";
        final var expectedName = "video.mp4";
        final var expectedRawLocation = "/tmp/videos";
        final var expectedEncodedLocation = "/tmp/videos/encoded";
        final var expectedMediaStatus = MediaStatus.PENDING;

        final var actual = AudioVideoMedia.with(
            expectedChecksum,
            expectedName,
            expectedRawLocation,
            expectedEncodedLocation,
            expectedMediaStatus
        );

        assertNotNull(actual);
        assertEquals(expectedChecksum, actual.checksum());
        assertEquals(expectedName, actual.name());
        assertEquals(expectedRawLocation, actual.rawLocation());
        assertEquals(expectedEncodedLocation, actual.encodedLocation());
        assertEquals(expectedMediaStatus, actual.status());
    }

    @Test
    void givenTwoVideosWithSameChecksumAndRawLocation_whenCallsEquals_shouldReturnTrue() {
        final var expectedChecksum = "a1s2d3";
        final var expectedRawLocation = "/tmp/images";

        final var firstVideo = AudioVideoMedia.with(
            expectedChecksum,
            "first-video.mp4",
            expectedRawLocation,
            "/videos/encoded",
            MediaStatus.PROCESSING
        );

        final var secondVideo = AudioVideoMedia.with(
            expectedChecksum,
            "second-video.mp4",
            expectedRawLocation,
            "/tmp/videos/encoded",
            MediaStatus.COMPLETED
        );


        assertNotNull(firstVideo);
        assertNotNull(secondVideo);
        assertEquals(firstVideo, secondVideo);
        assertNotSame(firstVideo, secondVideo);
    }

    @Test
    void givenTwoVideosWithoutSameChecksumAndRawLocation_whenCallsEquals_shouldReturnFalse() {
        final var expectedName = "video.mp4";
        final var expectedEncodedLocation = "/tmp/videos/encoded";
        final var expectedMediaStatus = MediaStatus.PENDING;

        final var firstVideo = AudioVideoMedia.with(
            "a1s2d3",
            expectedName,
            "/tmp/videos",
            expectedEncodedLocation,
            expectedMediaStatus
        );

        final var secondVideo = AudioVideoMedia.with(
            "z1x2c3",
            expectedName,
            "/tmp/medias/videos",
            expectedEncodedLocation,
            expectedMediaStatus
        );

        assertNotNull(firstVideo);
        assertNotNull(secondVideo);
        assertNotEquals(firstVideo, secondVideo);
        assertNotSame(firstVideo, secondVideo);
    }

    @Test
    void givenInvalidParams_whenCreateAudioVideoMedia_shouldThrownAnException() {
        final var expectedChecksum = "a1s2d3";
        final var expectedName = "video.mp4";
        final var expectedRawLocation = "/tmp/videos";
        final var expectedEncodedLocation = "/tmp/videos/encoded";
        final var expectedMediaStatus = MediaStatus.PENDING;

        assertThrows(
            NullPointerException.class,
            () -> AudioVideoMedia.with(null, expectedName, expectedRawLocation, expectedEncodedLocation, expectedMediaStatus)
        );

        assertThrows(
            NullPointerException.class,
            () -> AudioVideoMedia.with(expectedChecksum, null, expectedRawLocation, expectedEncodedLocation, expectedMediaStatus)
        );

        assertThrows(
            NullPointerException.class,
            () -> AudioVideoMedia.with(expectedChecksum, expectedName, null, expectedEncodedLocation, expectedMediaStatus)
        );

        assertThrows(
            NullPointerException.class,
            () -> AudioVideoMedia.with(expectedChecksum, expectedName, expectedRawLocation, null, expectedMediaStatus)
        );

        assertThrows(
            NullPointerException.class,
            () -> AudioVideoMedia.with(expectedChecksum, expectedName, expectedRawLocation, expectedEncodedLocation, null)
        );
    }

}
