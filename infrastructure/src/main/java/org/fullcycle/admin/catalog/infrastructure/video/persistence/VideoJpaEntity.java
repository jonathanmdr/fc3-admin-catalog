package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.video.Rating;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "videos")
@Entity(name = "Video")
public class VideoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "year_launched", nullable = false)
    private int yearLaunched;

    @Column(name = "opened", nullable = false)
    private boolean opened;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "rating", nullable = false)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(name = "duration", nullable = false, precision = 2)
    private double duration;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private AudioVideoMediaJpaEntity video;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "trailer_id")
    private AudioVideoMediaJpaEntity trailer;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "banner_id")
    private ImageMediaJpaEntity banner;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private ImageMediaJpaEntity thumbnail;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_half_id")
    private ImageMediaJpaEntity thumbnailHalf;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCategoryJpaEntity> categories;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoGenreJpaEntity> genres;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCastMemberJpaEntity> castMembers;

    public VideoJpaEntity() { }

    private VideoJpaEntity(
        final UUID id,
        final String title,
        final String description,
        final int yearLaunched,
        final boolean opened,
        final boolean published,
        final Rating rating,
        final double duration,
        final Instant createdAt,
        final Instant updatedAt,
        final AudioVideoMediaJpaEntity video,
        final AudioVideoMediaJpaEntity trailer,
        final ImageMediaJpaEntity banner,
        final ImageMediaJpaEntity thumbnail,
        final ImageMediaJpaEntity thumbnailHalf
        ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.yearLaunched = yearLaunched;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.video = video;
        this.trailer = trailer;
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.categories = new HashSet<>(3);
        this.genres = new HashSet<>(3);
        this.castMembers = new HashSet<>(3);
    }

    public static VideoJpaEntity from(final Video video) {
        final var entity = new VideoJpaEntity(
            UUID.fromString(video.getId().getValue()),
            video.getTitle(),
            video.getDescription(),
            video.getLaunchedAt().getValue(),
            video.isOpened(),
            video.isPublished(),
            video.getRating(),
            video.getDuration(),
            video.getCreatedAt(),
            video.getUpdatedAt(),
            video.getVideo()
                .map(AudioVideoMediaJpaEntity::from)
                .orElse(null),
            video.getTrailer()
                .map(AudioVideoMediaJpaEntity::from)
                .orElse(null),
            video.getBanner()
                .map(ImageMediaJpaEntity::from)
                .orElse(null),
            video.getThumbnail()
                .map(ImageMediaJpaEntity::from)
                .orElse(null),
            video.getThumbnailHalf()
                .map(ImageMediaJpaEntity::from)
                .orElse(null)
        );

        video.getCategories()
            .forEach(entity::addCategory);
        video.getGenres()
            .forEach(entity::addGenre);
        video.getCastMembers()
            .forEach(entity::addCastMember);

        return entity;
    }

    public Video toAggregate() {
        return Video.with(
            VideoID.from(getId()),
            getTitle(),
            getDescription(),
            Year.of(getYearLaunched()),
            getDuration(),
            getRating(),
            isOpened(),
            isPublished(),
            getCreatedAt(),
            getUpdatedAt(),
            Optional.ofNullable(getBanner())
                .map(ImageMediaJpaEntity::toDomain)
                .orElse(null),
            Optional.ofNullable(getThumbnail())
                .map(ImageMediaJpaEntity::toDomain)
                .orElse(null),
            Optional.ofNullable(getThumbnailHalf())
                .map(ImageMediaJpaEntity::toDomain)
                .orElse(null),
            Optional.ofNullable(getTrailer())
                .map(AudioVideoMediaJpaEntity::toDomain)
                .orElse(null),
            Optional.ofNullable(getVideo())
                .map(AudioVideoMediaJpaEntity::toDomain)
                .orElse(null),
            getCategories().stream()
                .map(it -> CategoryID.from(it.getId().getCategoryId()))
                .collect(Collectors.toSet()),
            getGenres().stream()
                .map(it -> GenreID.from(it.getId().getGenreId()))
                .collect(Collectors.toSet()),
            getCastMembers().stream()
                .map(it -> CastMemberID.from(it.getId().getCastMemberId()))
                .collect(Collectors.toSet())
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getYearLaunched() {
        return yearLaunched;
    }

    public void setYearLaunched(final int yearLaunched) {
        this.yearLaunched = yearLaunched;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(final boolean opened) {
        this.opened = opened;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(final boolean published) {
        this.published = published;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(final Rating rating) {
        this.rating = rating;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(final double duration) {
        this.duration = duration;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public void setVideo(final AudioVideoMediaJpaEntity video) {
        this.video = video;
    }

    public AudioVideoMediaJpaEntity getTrailer() {
        return trailer;
    }

    public void setTrailer(final AudioVideoMediaJpaEntity trailer) {
        this.trailer = trailer;
    }

    public ImageMediaJpaEntity getBanner() {
        return banner;
    }

    public void setBanner(final ImageMediaJpaEntity banner) {
        this.banner = banner;
    }

    public ImageMediaJpaEntity getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final ImageMediaJpaEntity thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageMediaJpaEntity getThumbnailHalf() {
        return thumbnailHalf;
    }

    public void setThumbnailHalf(final ImageMediaJpaEntity thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
    }

    public Set<VideoCategoryJpaEntity> getCategories() {
        return categories;
    }

    public Set<VideoGenreJpaEntity> getGenres() {
        return genres;
    }

    public void setGenres(final Set<VideoGenreJpaEntity> genres) {
        this.genres = genres;
    }

    public void setCategories(final Set<VideoCategoryJpaEntity> categories) {
        this.categories = categories;
    }

    public Set<VideoCastMemberJpaEntity> getCastMembers() {
        return castMembers;
    }

    public void setCastMembers(final Set<VideoCastMemberJpaEntity> castMembers) {
        this.castMembers = castMembers;
    }

    private void addCategory(final CategoryID categoryID) {
        this.categories.add(VideoCategoryJpaEntity.from(this, categoryID));
    }

    private void addGenre(final GenreID genreID) {
        this.genres.add(VideoGenreJpaEntity.from(this, genreID));
    }

    private void addCastMember(final CastMemberID castMemberID) {
        this.castMembers.add(VideoCastMemberJpaEntity.from(this, castMemberID));
    }

}
