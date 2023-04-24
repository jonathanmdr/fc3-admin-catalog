package org.fullcycle.admin.catalog.application.video.create;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.InternalErrorException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;
import org.fullcycle.admin.catalog.domain.video.MediaResourceGateway;
import org.fullcycle.admin.catalog.domain.video.Rating;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultCreateVideoUseCase extends CreateVideoUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;
    private final CastMemberGateway castMemberGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public DefaultCreateVideoUseCase(
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
    public CreateVideoOutput execute(final CreateVideoCommand command) {
        final var rating = Rating.of(command.rating()).orElse(null);
        final var launchedAt = Objects.isNull(command.launchedAt()) ? null : Year.of(command.launchedAt());
        final var categories = toIdentifier(command.categories(), CategoryID::from);
        final var genres = toIdentifier(command.genres(), GenreID::from);
        final var castMembers = toIdentifier(command.castMembers(), CastMemberID::from);

        final var notification = NotificationHandler.create();
        notification.append(validateCategories(categories));
        notification.append(validateGenres(genres));
        notification.append(validateCastMembers(castMembers));

        final var video = Video.newVideo(
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

        video.validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create aggregate video", notification);
        }

        return CreateVideoOutput.from(
            create(command, video)
        );
    }

    private Video create(final CreateVideoCommand command, final Video video) {
        final var videoId = video.getId();

        try {
            final var videoMedia = command.video()
                .map(resource -> this.mediaResourceGateway.storeAudioVideo(videoId, resource));

            final var trailerMedia = command.trailer()
                .map(resource -> this.mediaResourceGateway.storeAudioVideo(videoId, resource));

            final var bannerMedia = command.banner()
                .map(resource -> this.mediaResourceGateway.storeImage(videoId, resource));

            final var thumbnailMedia = command.thumbnail()
                .map(resource -> this.mediaResourceGateway.storeImage(videoId, resource));

            final var thumbnailHalfMedia = command.thumbnailHalf()
                .map(resource -> this.mediaResourceGateway.storeImage(videoId, resource));

            video.addAudioVideoMediaVideo(videoMedia.orElse(null));
            video.addAudioVideoMediaTrailer(trailerMedia.orElse(null));
            video.addImageMediaBanner(bannerMedia.orElse(null));
            video.addImageMediaThumbnail(thumbnailMedia.orElse(null));
            video.addImageMediaThumbnailHalf(thumbnailHalfMedia.orElse(null));

            return this.videoGateway.create(video);
        } catch (final Throwable throwable) {
            this.mediaResourceGateway.clearResources(videoId);
            throw InternalErrorException.with(
                "An error has occurred on creating a video with ID: %s".formatted(videoId.getValue()),
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
