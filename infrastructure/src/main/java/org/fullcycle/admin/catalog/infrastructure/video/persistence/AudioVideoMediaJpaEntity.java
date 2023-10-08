package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.MediaStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "videos_video_media")
@Entity(name = "AudioVideoMedia")
public class AudioVideoMediaJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "encoded_path", nullable = false)
    private String encodedPath;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    public AudioVideoMediaJpaEntity() { }

    private AudioVideoMediaJpaEntity(
        final String id,
        final String name,
        final String checksum,
        final String filePath,
        final String encodedPath,
        final MediaStatus status
    ) {
        this.id = id;
        this.name = name;
        this.checksum = checksum;
        this.filePath = filePath;
        this.encodedPath = encodedPath;
        this.status = status;
    }

    public static AudioVideoMediaJpaEntity from(final AudioVideoMedia audioVideoMedia) {
        return new AudioVideoMediaJpaEntity(
            audioVideoMedia.id(),
            audioVideoMedia.name(),
            audioVideoMedia.checksum(),
            audioVideoMedia.rawLocation(),
            audioVideoMedia.encodedLocation(),
            audioVideoMedia.status()
        );
    }

    public AudioVideoMedia toDomain() {
        return AudioVideoMedia.with(
            getId(),
            getName(),
            getChecksum(),
            getFilePath(),
            getEncodedPath(),
            getStatus()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public void setEncodedPath(final String encodedPath) {
        this.encodedPath = encodedPath;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(final MediaStatus status) {
        this.status = status;
    }

}
