package org.fullcycle.admin.catalog.infrastructure.video;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoResource;
import org.fullcycle.admin.catalog.infrastructure.services.StorageService;
import org.fullcycle.admin.catalog.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class DefaultMediaResourceGatewayTest {

    @Autowired
    private MediaResourceGateway mediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        storageService().reset();
    }

    @Test
    void testInjection() {
        assertNotNull(mediaResourceGateway);
        assertInstanceOf(DefaultMediaResourceGateway.class, mediaResourceGateway);

        assertNotNull(storageService);
        assertInstanceOf(InMemoryStorageService.class, storageService);
    }

    @Test
    void givenValidResource_whenCallsStorageAudioVideo_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.VIDEO;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedEncodedLocation = "";

        final var actualMedia = this.mediaResourceGateway.storeAudioVideo(expectedVideoId, VideoResource.with(expectedResource, expectedType));

        assertNotNull(actualMedia.id());
        assertEquals(expectedLocation, actualMedia.rawLocation());
        assertEquals(expectedResource.name(), actualMedia.name());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());
        assertEquals(expectedStatus, actualMedia.status());
        assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());

        final var actualStored = storageService().storage().get(expectedLocation);

        assertEquals(expectedResource, actualStored);
    }

    @Test
    void givenValidResource_whenCallsStorageImage_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.BANNER;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());

        final var actualMedia = this.mediaResourceGateway.storeImage(expectedVideoId, VideoResource.with(expectedResource, expectedType));

        assertNotNull(actualMedia.id());
        assertEquals(expectedLocation, actualMedia.location());
        assertEquals(expectedResource.name(), actualMedia.name());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());

        final var actualStored = storageService().storage().get(expectedLocation);

        assertEquals(expectedResource, actualStored);
    }

    @Test
    void givenValidVideoId_whenCallsGetResource_shouldReturnIt() {
        final var videoOne = VideoID.unique();
        final var expectedType = MediaType.VIDEO;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);

        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), expectedType), expectedResource);
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.TRAILER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.BANNER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));

        assertEquals(3, storageService().storage().size());

        final var actualResult = this.mediaResourceGateway.getResource(videoOne, expectedType)
            .orElseThrow(IllegalStateException::new);

        assertEquals(expectedResource, actualResult);
    }

    @Test
    void givenInvalidType_whenCallsGetResource_shouldReturnEmpty() {
        final var videoOne = VideoID.unique();
        final var expectedType = MediaType.THUMBNAIL;

        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.VIDEO.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.TRAILER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.BANNER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));

        assertEquals(3, storageService().storage().size());

        final var actualResult = this.mediaResourceGateway.getResource(videoOne, expectedType);

        Assertions.assertTrue(actualResult.isEmpty());
    }

    @Test
    void givenValidVideoId_whenCallsClearResources_shouldDeleteAll() {
        final var videoOne = VideoID.unique();
        final var videoTwo = VideoID.unique();

        final var toBeDeleted = new ArrayList<String>();
        toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.VIDEO.name()));
        toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.TRAILER.name()));
        toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.BANNER.name()));

        final var expectedValues = new ArrayList<String>();
        expectedValues.add("videoId-%s/type-%s".formatted(videoTwo.getValue(), MediaType.VIDEO.name()));
        expectedValues.add("videoId-%s/type-%s".formatted(videoTwo.getValue(), MediaType.BANNER.name()));

        toBeDeleted.forEach(id -> storageService().store(id, Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType())));
        expectedValues.forEach(id -> storageService().store(id, Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType())));

        assertEquals(5, storageService().storage().size());

        this.mediaResourceGateway.clearResources(videoOne);

        assertEquals(2, storageService().storage().size());

        final var actualKeys = storageService().storage().keySet();

        Assertions.assertTrue(
            expectedValues.size() == actualKeys.size()
                && actualKeys.containsAll(expectedValues)
        );
    }

    private InMemoryStorageService storageService() {
        return (InMemoryStorageService) storageService;
    }

}
