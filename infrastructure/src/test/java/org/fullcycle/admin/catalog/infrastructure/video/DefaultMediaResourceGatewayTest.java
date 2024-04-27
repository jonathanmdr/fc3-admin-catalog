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
        // given
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.VIDEO;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedEncodedLocation = "";

        // when
        final var actualMedia = this.mediaResourceGateway.storeAudioVideo(expectedVideoId, VideoResource.with(expectedResource, expectedType));

        // then
        assertNotNull(actualMedia.id());
        Assertions.assertEquals(expectedLocation, actualMedia.rawLocation());
        Assertions.assertEquals(expectedResource.name(), actualMedia.name());
        Assertions.assertEquals(expectedResource.checksum(), actualMedia.checksum());
        Assertions.assertEquals(expectedStatus, actualMedia.status());
        Assertions.assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());

        final var actualStored = storageService().storage().get(expectedLocation);

        Assertions.assertEquals(expectedResource, actualStored);
    }

    @Test
    void givenValidResource_whenCallsStorageImage_shouldStoreIt() {
        // given
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.BANNER;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());

        // when
        final var actualMedia = this.mediaResourceGateway.storeImage(expectedVideoId, VideoResource.with(expectedResource, expectedType));

        // then
        assertNotNull(actualMedia.id());
        Assertions.assertEquals(expectedLocation, actualMedia.location());
        Assertions.assertEquals(expectedResource.name(), actualMedia.name());
        Assertions.assertEquals(expectedResource.checksum(), actualMedia.checksum());

        final var actualStored = storageService().storage().get(expectedLocation);

        Assertions.assertEquals(expectedResource, actualStored);
    }

    @Test
    void givenValidVideoId_whenCallsGetResource_shouldReturnIt() {
        // given
        final var videoOne = VideoID.unique();
        final var expectedType = MediaType.VIDEO;
        final var expectedResource = Fixtures.ResourceFixture.resource(expectedType);

        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), expectedType), expectedResource);
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.TRAILER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.BANNER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));

        Assertions.assertEquals(3, storageService().storage().size());

        // when
        final var actualResult = this.mediaResourceGateway.getResource(videoOne, expectedType).get();

        // then
        Assertions.assertEquals(expectedResource, actualResult);
    }

    @Test
    void givenInvalidType_whenCallsGetResource_shouldReturnEmpty() {
        // given
        final var videoOne = VideoID.unique();
        final var expectedType = MediaType.THUMBNAIL;

        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.VIDEO.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.TRAILER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));
        storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(), MediaType.BANNER.name()), Fixtures.ResourceFixture.resource(Fixtures.VideoFixture.mediaType()));

        Assertions.assertEquals(3, storageService().storage().size());

        // when
        final var actualResult = this.mediaResourceGateway.getResource(videoOne, expectedType);

        // then
        Assertions.assertTrue(actualResult.isEmpty());
    }

    @Test
    void givenValidVideoId_whenCallsClearResources_shouldDeleteAll() {
        // given
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

        Assertions.assertEquals(5, storageService().storage().size());

        // when
        this.mediaResourceGateway.clearResources(videoOne);

        // then
        Assertions.assertEquals(2, storageService().storage().size());

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
