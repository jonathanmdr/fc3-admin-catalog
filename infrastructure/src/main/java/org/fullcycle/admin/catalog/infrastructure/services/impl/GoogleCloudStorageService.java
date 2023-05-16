package org.fullcycle.admin.catalog.infrastructure.services.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.apache.commons.lang3.StringUtils;
import org.fullcycle.admin.catalog.domain.resource.Resource;
import org.fullcycle.admin.catalog.domain.utils.CollectionUtils;
import org.fullcycle.admin.catalog.infrastructure.services.StorageService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class GoogleCloudStorageService implements StorageService {

    private final String bucketName;
    private final Storage storage;

    public GoogleCloudStorageService(final String bucketName, final Storage storage) {
        this.bucketName = Objects.requireNonNull(bucketName, "bucketName cannot be null");
        this.storage = Objects.requireNonNull(storage, "storage cannot be null");
    }

    @Override
    public Optional<Resource> get(final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return Optional.empty();
        }

        return Optional.ofNullable(
            this.storage.get(this.bucketName, fileName)
        ).map(
            blob -> Resource.with(
                blob.getCrc32cToHexString(),
                blob.getContent(),
                blob.getContentType(),
                blob.getName()
            )
        );
    }

    @Override
    public List<String> findAll(final String prefixName) {
        if (StringUtils.isBlank(prefixName)) {
            return Collections.emptyList();
        }

        final var blobs = this.storage.list(
            this.bucketName,
            Storage.BlobListOption.prefix(prefixName)
        );

        return StreamSupport.stream(blobs.iterateAll().spliterator(), false)
            .map(BlobInfo::getBlobId)
            .map(BlobId::getName)
            .toList();
    }

    @Override
    public void store(final String fileName, final Resource resource) {
        Objects.requireNonNull(fileName, "fileName cannot be null");
        Objects.requireNonNull(resource, "resource cannot be null");

        final var blobInfo = BlobInfo.newBuilder(this.bucketName, fileName)
            .setContentType(resource.contentType())
            .setCrc32cFromHexString(resource.checksum())
            .build();

        this.storage.create(blobInfo, resource.content());
    }

    @Override
    public void deleteAll(final Collection<String> fileNames) {
        if (CollectionUtils.isNonNullAndNonEmpty(fileNames)) {
            final var blobIds = fileNames.stream()
                .map(fileName -> BlobId.of(this.bucketName, fileName))
                .toList();

            this.storage.delete(blobIds);
        }
    }

}
