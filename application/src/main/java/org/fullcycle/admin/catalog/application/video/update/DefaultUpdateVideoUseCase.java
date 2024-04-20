package org.fullcycle.admin.catalog.application.video.update;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;
import org.fullcycle.admin.catalog.domain.video.*;

import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.fullcycle.admin.catalog.domain.video.MediaType.*;

public class DefaultUpdateVideoUseCase extends UpdateVideoUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;
    private final CastMemberGateway castMemberGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public DefaultUpdateVideoUseCase(
        final CategoryGateway categoryGateway,
        final GenreGateway genreGateway,
        final CastMemberGateway castMemberGateway,
        final MediaResourceGateway mediaResourceGateway,
        final VideoGateway videoGateway
    ) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public UpdateVideoOutput execute(final UpdateVideoCommand command) {
        final var id = VideoID.from(command.id());
        final var rating = Rating.of(command.rating()).orElse(null);
        final var launchedAt = Objects.isNull(command.launchedAt()) ? null : Year.of(command.launchedAt());
        final var categories = toIdentifier(command.categories(), CategoryID::from);
        final var genres = toIdentifier(command.genres(), GenreID::from);
        final var castMembers = toIdentifier(command.castMembers(), CastMemberID::from);

        final var foundVideo = this.videoGateway.findById(id)
            .orElseThrow(notFoundException(id));

        final var notification = NotificationHandler.create();
        notification.append(validateCategories(categories));
        notification.append(validateGenres(genres));
        notification.append(validateCastMembers(castMembers));

        final var updatedVideo = foundVideo.update(
            command.title(),
            command.description(),
            launchedAt,
            command.duration(),
            rating,
            command.opened(),
            command.published(),
            categories,
            genres,
            castMembers
        );

        foundVideo.validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Could not update aggregate video", notification);
        }

        return UpdateVideoOutput.from(
            update(command, updatedVideo)
        );
    }

    private Supplier<DomainException> notFoundException(final VideoID id) {
        return () -> NotFoundException.with(Video.class, id);
    }

    private Video update(final UpdateVideoCommand command, final Video video) {
        final var videoId = video.getId();

        try {
            final var videoMedia = command.video()
                .map(resource -> this.mediaResourceGateway.storeAudioVideo(videoId, VideoResource.with(resource, VIDEO)));

            final var trailerMedia = command.trailer()
                .map(resource -> this.mediaResourceGateway.storeAudioVideo(videoId, VideoResource.with(resource, TRAILER)));

            final var bannerMedia = command.banner()
                .map(resource -> this.mediaResourceGateway.storeImage(videoId, VideoResource.with(resource, BANNER)));

            final var thumbnailMedia = command.thumbnail()
                .map(resource -> this.mediaResourceGateway.storeImage(videoId, VideoResource.with(resource, THUMBNAIL)));

            final var thumbnailHalfMedia = command.thumbnailHalf()
                .map(resource -> this.mediaResourceGateway.storeImage(videoId, VideoResource.with(resource, THUMBNAIL_HALF)));

            videoMedia.ifPresent(video::updateVideoMedia);
            trailerMedia.ifPresent(video::updateTrailerMedia);
            bannerMedia.ifPresent(video::updateBannerMedia);
            thumbnailMedia.ifPresent(video::updateThumbnailMedia);
            thumbnailHalfMedia.ifPresent(video::updateThumbnailHalfMedia);

            return this.videoGateway.update(video);
        } catch (final Throwable throwable) {
            throw InternalErrorException.with(
                "An error has occurred on updating a video with ID: %s".formatted(videoId.getValue()),
                throwable
            );
        }
    }

    private <T> Set<T> toIdentifier(final Set<String> ids, final Function<String, T> mapper) {
        if (Objects.isNull(ids)) {
            return Collections.emptySet();
        }

        return ids.stream()
            .map(mapper)
            .collect(Collectors.toSet());
    }

    private ValidationHandler validateCategories(final Set<CategoryID> categoryIds) {
        return validateExternalAggregateIds(
            categoryIds,
            this.categoryGateway::existsByIds,
            "categories"
        );
    }

    private ValidationHandler validateGenres(final Set<GenreID> genreIds) {
        return validateExternalAggregateIds(
            genreIds,
            this.genreGateway::existsByIds,
            "genres"
        );
    }

    private ValidationHandler validateCastMembers(final Set<CastMemberID> castMemberIds) {
        return validateExternalAggregateIds(
            castMemberIds,
            this.castMemberGateway::existsByIds,
            "cast members"
        );
    }

    private <T extends Identifier> ValidationHandler validateExternalAggregateIds(
        final Set<T> ids,
        final Function<Iterable<T>, List<T>> existsByIds,
        final String aggregateName
    ) {
        final var notification = NotificationHandler.create();
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return notification;
        }

        final var retrievedIds = existsByIds.apply(ids);

        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsToMessage = missingIds.stream()
                .map(Identifier::getValue)
                .collect(Collectors.joining(", "));

            notification.append(new Error("Some %s could not be found: %s".formatted(aggregateName, missingIdsToMessage)));
        }

        return notification;
    }

}
