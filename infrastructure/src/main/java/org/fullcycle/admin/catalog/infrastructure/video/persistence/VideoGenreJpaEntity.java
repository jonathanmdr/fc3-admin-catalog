package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.genre.GenreID;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;

@Table(name = "videos_genres")
@Entity(name = "VideoGenre")
public class VideoGenreJpaEntity {

    @EmbeddedId
    private VideoGenreID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoGenreJpaEntity() { }

    private VideoGenreJpaEntity(final VideoGenreID id, final VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoGenreJpaEntity from(final VideoJpaEntity videoJpaEntity, final GenreID genreID) {
        return new VideoGenreJpaEntity(
            VideoGenreID.from(videoJpaEntity.getId(), genreID.getValue()),
            videoJpaEntity
        );
    }

    public VideoGenreID getId() {
        return id;
    }

    public void setId(final VideoGenreID id) {
        this.id = id;
    }

    public VideoJpaEntity getVideo() {
        return video;
    }

    public void setVideo(final VideoJpaEntity video) {
        this.video = video;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoGenreJpaEntity that = (VideoGenreJpaEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(video, that.video);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, video);
    }

}
