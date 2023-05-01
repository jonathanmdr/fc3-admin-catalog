package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VideoGenreID implements Serializable {

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "genre_id", nullable = false)
    private UUID genreId;

    public VideoGenreID() { }

    private VideoGenreID(final UUID videoId, final UUID genreId) {
        this.videoId = videoId;
        this.genreId = genreId;
    }

    public static VideoGenreID from(final UUID videoId, final UUID genreId) {
        return new VideoGenreID(videoId, genreId);
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(final UUID videoId) {
        this.videoId = videoId;
    }

    public UUID getGenreId() {
        return genreId;
    }

    public void setGenreId(final UUID genreId) {
        this.genreId = genreId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoGenreID that = (VideoGenreID) o;
        return Objects.equals(videoId, that.videoId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, genreId);
    }

}
