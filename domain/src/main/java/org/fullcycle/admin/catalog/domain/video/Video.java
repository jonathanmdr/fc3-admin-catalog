package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.AggregateRoot;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Video extends AggregateRoot<VideoID> {

    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private Rating rating;

    private boolean opened;
    private boolean published;

    private final Instant createdAt;
    private Instant updatedAt;

    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;

    private AudioVideoMedia trailer;
    private AudioVideoMedia video;

    private Set<CategoryID> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;

    private Video(
        final VideoID id,
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Instant createdAt,
        final Instant updatedAt,
        final ImageMedia banner,
        final ImageMedia thumbnail,
        final ImageMedia thumbnailHalf,
        final AudioVideoMedia trailer,
        final AudioVideoMedia video,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        super(id);
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.rating = rating;
        this.opened = opened;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.trailer = trailer;
        this.video = video;
        this.categories = Objects.isNull(categories) ? new HashSet<>() : new HashSet<>(categories);
        this.genres = Objects.isNull(genres) ? new HashSet<>() : new HashSet<>(genres);
        this.castMembers = Objects.isNull(castMembers) ? new HashSet<>() : new HashSet<>(castMembers);
    }

    public static Video newVideo(
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        final var id = VideoID.unique();
        final var now = Instant.now();
        return new Video(
            id,
            title,
            description,
            launchedAt,
            duration,
            rating,
            opened,
            published,
            now,
            now,
            null,
            null,
            null,
            null,
            null,
            categories,
            genres,
            castMembers
        );
    }

    public static Video with(
        final VideoID id,
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Instant createdAt,
        final Instant updatedAt,
        final ImageMedia banner,
        final ImageMedia thumbnail,
        final ImageMedia thumbnailHalf,
        final AudioVideoMedia trailer,
        final AudioVideoMedia video,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        return new Video(
            id,
            title,
            description,
            launchedAt,
            duration,
            rating,
            opened,
            published,
            createdAt,
            updatedAt,
            banner,
            thumbnail,
            thumbnailHalf,
            trailer,
            video,
            categories,
            genres,
            castMembers
        );
    }

    public static Video with(final Video video) {
        return with(
            video.getId(),
            video.getTitle(),
            video.getDescription(),
            video.getLaunchedAt(),
            video.getDuration(),
            video.getRating(),
            video.isOpened(),
            video.isPublished(),
            video.getCreatedAt(),
            video.getUpdatedAt(),
            video.getBanner().orElse(null),
            video.getThumbnail().orElse(null),
            video.getThumbnailHalf().orElse(null),
            video.getTrailer().orElse(null),
            video.getVideo().orElse(null),
            video.getCategories(),
            video.getGenres(),
            video.getCastMembers()
        );
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new VideoValidator(this, handler).validate();
    }

    public Video update(
        final String title,
        final String description,
        final Year launchedAt,
        final double duration,
        final Rating rating,
        final boolean opened,
        final boolean published,
        final Set<CategoryID> categories,
        final Set<GenreID> genres,
        final Set<CastMemberID> castMembers
    ) {
        this.title = title;
        this.description = description;
        this.launchedAt = launchedAt;
        this.duration = duration;
        this.rating = rating;
        this.opened = opened;
        this.published = published;
        this.updatedAt = Instant.now();
        this.categories = Objects.isNull(categories) ? new HashSet<>() : new HashSet<>(categories);
        this.genres = Objects.isNull(genres) ? new HashSet<>() : new HashSet<>(genres);
        this.castMembers = Objects.isNull(castMembers) ? new HashSet<>() : new HashSet<>(castMembers);
        return this;
    }

    public void addImageMediaBanner(final ImageMedia media) {
        this.banner = Objects.requireNonNull(media);
        this.updatedAt = Instant.now();
    }

    public void removeImageMediaBanner() {
        this.banner = null;
        this.updatedAt = Instant.now();
    }

    public void addImageMediaThumbnail(final ImageMedia media) {
        this.thumbnail = Objects.requireNonNull(media);
        this.updatedAt = Instant.now();
    }

    public void removeImageMediaThumbnail() {
        this.thumbnail = null;
        this.updatedAt = Instant.now();
    }

    public void addImageMediaThumbnailHalf(final ImageMedia media) {
        this.thumbnailHalf = Objects.requireNonNull(media);
        this.updatedAt = Instant.now();
    }

    public void removeImageMediaThumbnailHalf() {
        this.thumbnailHalf = null;
        this.updatedAt = Instant.now();
    }

    public void addAudioVideoMediaTrailer(final AudioVideoMedia media) {
        this.trailer = Objects.requireNonNull(media);
        this.updatedAt = Instant.now();
    }

    public void removeAudioVideoMediaTrailer() {
        this.trailer = null;
        this.updatedAt = Instant.now();
    }

    public void addAudioVideoMediaVideo(final AudioVideoMedia media) {
        this.video = Objects.requireNonNull(media);
        this.updatedAt = Instant.now();
    }

    public void removeAudioVideoMediaVideo() {
        this.video = null;
        this.updatedAt = Instant.now();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public Rating getRating() {
        return rating;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isPublished() {
        return published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Optional<AudioVideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public Set<CategoryID> getCategories() {
        return Objects.isNull(categories) ? Collections.emptySet() : Collections.unmodifiableSet(categories);
    }

    public Set<GenreID> getGenres() {
        return Objects.isNull(genres) ? Collections.emptySet() : Collections.unmodifiableSet(genres);
    }

    public Set<CastMemberID> getCastMembers() {
        return Objects.isNull(castMembers) ? Collections.emptySet() : Collections.unmodifiableSet(castMembers);
    }

}
