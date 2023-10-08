package org.fullcycle.admin.catalog.infrastructure.video;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoResource;
import org.fullcycle.admin.catalog.infrastructure.services.StorageService;
import org.fullcycle.admin.catalog.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fullcycle.admin.catalog.domain.Fixtures.ResourceFixture.resource;
import static org.fullcycle.admin.catalog.domain.Fixtures.VideoFixture.mediaType;

@IntegrationTest
class DefaultMediaResourceGatewayTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private MediaResourceGateway gateway;

    @BeforeEach
    void setup() {
        ((InMemoryStorageService) storageService).reset();
    }

    @Test
    void assertInjection() {
        assertThat(storageService)
            .isNotNull()
            .isInstanceOf(InMemoryStorageService.class);

        assertThat(gateway)
            .isNotNull()
            .isInstanceOf(DefaultMediaResourceGateway.class);
    }

    @Test
    void givenAValidResource_whenCallsStorageAudioVideo_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.VIDEO;
        final var expectedResource = resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name().toLowerCase());
        final var expectedStatus = MediaStatus.PENDING;

        final var actual = this.gateway.storeAudioVideo(
            expectedVideoId,
            VideoResource.with(expectedResource, expectedType)
        );

        assertThat(actual.id()).isNotNull();
        assertThat(actual.name()).isEqualTo(expectedResource.name());
        assertThat(actual.checksum()).isEqualTo(expectedResource.checksum());
        assertThat(actual.rawLocation()).isEqualTo(expectedLocation);
        assertThat(actual.status()).isEqualTo(expectedStatus);
        assertThat(actual.encodedLocation()).isEmpty();

        final var storedResource = this.storageService.get(expectedLocation);

        assertThat(storedResource)
            .isPresent()
            .contains(expectedResource);
    }

    @Test
    void givenAValidResource_whenCallsStorageImage_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.BANNER;
        final var expectedResource = resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name().toLowerCase());

        final var actual = this.gateway.storeImage(
            expectedVideoId,
            VideoResource.with(expectedResource, expectedType)
        );

        assertThat(actual.id()).isNotNull();
        assertThat(actual.name()).isEqualTo(expectedResource.name());
        assertThat(actual.checksum()).isEqualTo(expectedResource.checksum());

        final var storedResource = this.storageService.get(expectedLocation);

        assertThat(storedResource)
            .isPresent()
            .contains(expectedResource);
    }

    @Test
    void givenAValidResource_whenCallsGetResource_thenReturnResource() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.BANNER;
        final var expectedResource = resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name().toLowerCase());

        this.storageService.store(
            expectedLocation,
            expectedResource
        );

        final var actual = this.gateway.getResource(expectedVideoId, expectedType);

        assertThat(actual).isPresent();
        assertThat(actual.get().content()).isNotNull();
        assertThat(actual.get().name()).isEqualTo(expectedResource.name());
        assertThat(actual.get().checksum()).isEqualTo(expectedResource.checksum());
    }

    @Test
    void givenAnInvalidResource_whenCallsGetResource_thenReturnEmpty() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = MediaType.BANNER;
        final var expectedResource = resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name().toLowerCase());

        this.storageService.store(
            expectedLocation,
            expectedResource
        );

        final var actual = this.gateway.getResource(VideoID.unique(), expectedType);

        assertThat(actual).isEmpty();
    }

    @Test
    void givenAValidVideoId_whenCallsClearResources_shouldDeleteAll() {
        final var videoToDelete = VideoID.unique();
        final var toBeDelete = new ArrayList<String>();
        toBeDelete.add("videoId-%s/type-%s".formatted(videoToDelete.getValue(), MediaType.VIDEO.name().toLowerCase()));
        toBeDelete.add("videoId-%s/type-%s".formatted(videoToDelete.getValue(), MediaType.TRAILER.name().toLowerCase()));
        toBeDelete.add("videoId-%s/type-%s".formatted(videoToDelete.getValue(), MediaType.BANNER.name().toLowerCase()));

        final var expectedVideo = VideoID.unique();
        final var expectedResources = new ArrayList<String>();
        expectedResources.add("videoId-%s/type-%s".formatted(expectedVideo.getValue(), MediaType.VIDEO.name().toLowerCase()));
        expectedResources.add("videoId-%s/type-%s".formatted(expectedVideo.getValue(), MediaType.BANNER.name().toLowerCase()));

        toBeDelete.forEach(id -> this.storageService.store(id, resource(mediaType())));
        expectedResources.forEach(id -> this.storageService.store(id, resource(mediaType())));

        final var inMemoryStorage = ((InMemoryStorageService) this.storageService);
        assertThat(inMemoryStorage.storage()).hasSize(5);

        this.gateway.clearResources(videoToDelete);

        assertThat(inMemoryStorage.storage()).hasSize(expectedResources.size());
        assertThat(inMemoryStorage.storage().keySet()).containsAll(expectedResources);
    }

}
