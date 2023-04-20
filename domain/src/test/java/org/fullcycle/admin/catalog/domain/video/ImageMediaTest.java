package org.fullcycle.admin.catalog.domain.video;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageMediaTest {

    @Test
    void givenValidParams_whenCreateANewImage_shouldInstantiate() {
        final var expectedChecksum = "a1s2d3";
        final var expectedName = "image.jpeg";
        final var expectedLocation = "/tmp/images";

        final var actual = ImageMedia.with(expectedChecksum, expectedName, expectedLocation);

        assertNotNull(actual);
        assertEquals(expectedChecksum, actual.checksum());
        assertEquals(expectedName, actual.name());
        assertEquals(expectedLocation, actual.location());
    }

    @Test
    void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_shouldReturnTrue() {
        final var expectedChecksum = "a1s2d3";
        final var expectedLocation = "/tmp/images";

        final var firstImage = ImageMedia.with(expectedChecksum, "bla.jpeg", expectedLocation);
        final var secondImage = ImageMedia.with(expectedChecksum, "foo.jpeg", expectedLocation);

        assertNotNull(firstImage);
        assertNotNull(secondImage);
        assertEquals(firstImage, secondImage);
        assertNotSame(firstImage, secondImage);
    }

    @Test
    void givenTwoImagesWithoutSameChecksumAndLocation_whenCallsEquals_shouldReturnFalse() {
        final var expectedLocation = "/tmp/images";

        final var firstImage = ImageMedia.with("a1d2s3", "bla.jpeg", expectedLocation);
        final var secondImage = ImageMedia.with("z1x2c3", "bla.jpeg", expectedLocation);

        assertNotNull(firstImage);
        assertNotNull(secondImage);
        assertNotEquals(firstImage, secondImage);
        assertNotSame(firstImage, secondImage);
    }

    @Test
    void givenInvalidParams_whenCreateImageMedia_shouldThrownAnException() {
        final var expectedChecksum = "a1s2d3";
        final var expectedName = "image.jpeg";
        final var expectedLocation = "/tmp/images";

        assertThrows(
            NullPointerException.class,
            () -> ImageMedia.with(null, expectedName, expectedLocation)
        );

        assertThrows(
            NullPointerException.class,
            () -> ImageMedia.with(expectedChecksum, null, expectedLocation)
        );

        assertThrows(
            NullPointerException.class,
            () -> ImageMedia.with(expectedChecksum, expectedName, null)
        );
    }

}
