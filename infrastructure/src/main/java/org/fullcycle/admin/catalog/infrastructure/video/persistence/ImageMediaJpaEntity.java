package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.video.ImageMedia;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "videos_image_media")
@Entity(name = "ImageMedia")
public class ImageMediaJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    public ImageMediaJpaEntity() { }

    private ImageMediaJpaEntity(
        final String id,
        final String name,
        final String checksum,
        final String filePath
    ) {
        this.id = id;
        this.name = name;
        this.checksum = checksum;
        this.filePath = filePath;
    }

    public static ImageMediaJpaEntity from(final ImageMedia imageMedia) {
        return new ImageMediaJpaEntity(
            imageMedia.id(),
            imageMedia.name(),
            imageMedia.checksum(),
            imageMedia.location()
        );
    }

    public ImageMedia toDomain() {
        return ImageMedia.with(
            getId(),
            getName(),
            getChecksum(),
            getFilePath()
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

}
