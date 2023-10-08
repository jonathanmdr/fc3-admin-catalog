package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;

@Table(name = "videos_cast_members")
@Entity(name = "VideoCastMember")
public class VideoCastMemberJpaEntity {

    @EmbeddedId
    private VideoCastMemberID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoCastMemberJpaEntity() { }

    private VideoCastMemberJpaEntity(final VideoCastMemberID id, final VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoCastMemberJpaEntity from(final VideoJpaEntity videoJpaEntity, final CastMemberID castMemberID) {
        return new VideoCastMemberJpaEntity(
            VideoCastMemberID.from(videoJpaEntity.getId(), castMemberID.getValue()),
            videoJpaEntity
        );
    }

    public VideoCastMemberID getId() {
        return id;
    }

    public void setId(final VideoCastMemberID id) {
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
        final VideoCastMemberJpaEntity that = (VideoCastMemberJpaEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(video, that.video);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, video);
    }

}
