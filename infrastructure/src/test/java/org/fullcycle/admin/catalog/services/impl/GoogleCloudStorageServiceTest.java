package org.fullcycle.admin.catalog.services.impl;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.resource.Resource;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.infrastructure.services.impl.GoogleCloudStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoogleCloudStorageServiceTest {

    private static final String BUCKET_NAME = "fc3_test";

    private Storage storage;
    private GoogleCloudStorageService service;

    @BeforeEach
    void setup() {
        this.storage = mock(Storage.class);
        this.service = new GoogleCloudStorageService(BUCKET_NAME, this.storage);
    }

    @Test
    void givenAValidResource_whenCallsStore_shouldPersistIt() {
        final var expectedFileName = IdentifierUtils.unique();
        final var expectedResource = Fixtures.ResourceFixture.resource(MediaType.VIDEO);
        final var expectedBlob = mockBlob(expectedFileName, expectedResource);

        doReturn(expectedBlob)
            .when(this.storage).create(any(BlobInfo.class), any());

        this.service.store(expectedFileName, expectedResource);

        final var captor = ArgumentCaptor.forClass(BlobInfo.class);

        verify(this.storage).create(captor.capture(), eq(expectedResource.content()));

        final var actualBlob = captor.getValue();
        assertThat(actualBlob.getBlobId().getBucket()).isEqualTo(BUCKET_NAME);
        assertThat(actualBlob.getBlobId().getName()).isEqualTo(expectedFileName);
        assertThat(actualBlob.getName()).isEqualTo(expectedFileName);
        assertThat(actualBlob.getCrc32cToHexString()).isEqualTo(expectedResource.checksum());
        assertThat(actualBlob.getContentType()).isEqualTo(expectedResource.contentType());
    }

    @Test
    void givenAnInvalidParams_whenCallsStore_shouldBeThrownException() {
        final var resource = Fixtures.ResourceFixture.resource(MediaType.TRAILER);

        assertThatThrownBy(() -> this.service.store(null, resource))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fileName cannot be null");

        assertThatThrownBy(() -> this.service.store(resource.name(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("resource cannot be null");
    }

    @Test
    void givenAValidParam_whenCallsGet_shouldBeReturnResource() {
        final var expectedResource = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var expectedBlob = mockBlob(expectedResource.name(), expectedResource);

        doReturn(expectedBlob)
            .when(this.storage).get(anyString(), anyString());

        final var actual = this.service.get(expectedResource.name());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expectedResource);

        verify(this.storage).get(eq(BUCKET_NAME), eq(expectedResource.name()));
    }

    @Test
    void givenAnInvalidParam_whenCallsGet_shouldBeReturnEmpty() {
        doReturn(null)
            .when(this.storage).get(anyString(), anyString());

        var actual = this.service.get("invalid_name");

        assertThat(actual).isEmpty();
        verify(this.storage).get(eq(BUCKET_NAME), eq("invalid_name"));
        reset(this.storage);

        actual = this.service.get("  ");

        assertThat(actual).isEmpty();
        verify(this.storage, never()).get(anyString(), anyString());

        actual = this.service.get(null);

        assertThat(actual).isEmpty();
        verify(this.storage, never()).get(anyString(), anyString());
    }

    @Test
    void givenAValidParam_whenCallsFindAll_shouldBeReturnResourceNameList() {
        final var expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);

        final var expectedTrailerBlob = mockBlob(expectedTrailer.name(), expectedTrailer);
        final var expectedBannerBlob = mockBlob(expectedBanner.name(), expectedBanner);

        final var expectedPage = mock(Page.class);

        doReturn(expectedPage)
            .when(this.storage).list(anyString(), any());
        doReturn(List.of(expectedTrailerBlob, expectedBannerBlob))
            .when(expectedPage).iterateAll();

        final var expected = List.of(MediaType.TRAILER.name(), MediaType.BANNER.name());
        final var actual = this.service.findAll("er");

        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).isEqualTo(expected);

        verify(this.storage).list(eq(BUCKET_NAME), eq(Storage.BlobListOption.prefix("er")));
        verify(expectedPage).iterateAll();
    }

    @Test
    void givenAnInvalidParam_whenCallsFindAll_shouldBeReturnEmptyList() {
        final var expected = List.of();
        var actual = this.service.findAll(null);

        assertThat(actual).isEmpty();
        assertThat(actual).isEqualTo(expected);
        verify(this.storage, never()).list(any(), any());

        actual = this.service.findAll("  ");

        assertThat(actual).isEmpty();
        assertThat(actual).isEqualTo(expected);
        verify(this.storage, never()).list(any(), any());
    }

    @Test
    void givenAValidParam_whenCallsDeleteAll_shouldBeDeleteAllResources() {
        final var expectedTrailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var expectedBanner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var expectedTrailerBlob = mockBlob(expectedTrailer.name(), expectedTrailer);
        final var expectedBannerBlob = mockBlob(expectedBanner.name(), expectedBanner);
        final var expectedBlobIds = List.of(expectedTrailerBlob.getBlobId(), expectedBannerBlob.getBlobId());

        when(this.storage.delete(expectedBlobIds)).thenReturn(List.of(true, true));

        this.service.deleteAll(List.of(expectedTrailer.name(), expectedBanner.name()));

        verify(this.storage).delete(expectedBlobIds);
    }

    @Test
    void givenAnEmptyListParam_whenCallsDeleteAll_shouldBeDoNothing() {
        this.service.deleteAll(List.of());
        verify(this.storage, never()).delete();
    }

    private Blob mockBlob(final String fileName, final Resource resource) {
        final var blob = mock(Blob.class);
        when(blob.getBlobId()).thenReturn(BlobId.of(BUCKET_NAME, fileName));
        when(blob.getCrc32cToHexString()).thenReturn(resource.checksum());
        when(blob.getContent()).thenReturn(resource.content());
        when(blob.getContentType()).thenReturn(resource.contentType());
        when(blob.getName()).thenReturn(resource.name());
        return blob;
    }

}
