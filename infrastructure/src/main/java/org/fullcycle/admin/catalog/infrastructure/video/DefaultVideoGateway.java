package org.fullcycle.admin.catalog.infrastructure.video;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoPreview;
import org.fullcycle.admin.catalog.domain.video.VideoSearchQuery;
import org.fullcycle.admin.catalog.infrastructure.utils.SqlUtils;
import org.fullcycle.admin.catalog.infrastructure.video.persistence.VideoJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static org.fullcycle.admin.catalog.domain.utils.CollectionUtils.mapTo;
import static org.fullcycle.admin.catalog.domain.utils.CollectionUtils.nullIfEmpty;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final VideoRepository videoRepository;

    public DefaultVideoGateway(final VideoRepository videoRepository) {
        this.videoRepository = Objects.requireNonNull(videoRepository);
    }

    @Transactional
    @Override
    public Video create(final Video video) {
        return save(video);
    }

    @Transactional
    @Override
    public Video update(final Video video) {
        return save(video);
    }

    @Override
    public void deleteById(final VideoID videoId) {
        final var id = videoId.getValue();
        if (this.videoRepository.existsById(id)) {
            this.videoRepository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Video> findById(final VideoID videoId) {
        return this.videoRepository.findById(videoId.getValue())
            .map(VideoJpaEntity::toAggregate);
    }

    @Transactional(readOnly = true)
    @Override
    public Pagination<VideoPreview> findAll(final VideoSearchQuery query) {
        final var page = PageRequest.of(
            query.page(),
            query.perPage(),
            Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var categories = nullIfEmpty(
            mapTo(query.categories(), CategoryID::getValue)
        );
        final var genres = nullIfEmpty(
            mapTo(query.genres(), GenreID::getValue)
        );
        final var castMembers = nullIfEmpty(
            mapTo(query.castMembers(), CastMemberID::getValue)
        );

        final var pageResult = this.videoRepository.findAll(
            SqlUtils.like(query.terms()),
            categories,
            genres,
            castMembers,
            page
        );

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.toList()
        );
    }

    private Video save(final Video video) {
        return this.videoRepository.save(
            VideoJpaEntity.from(video)
        ).toAggregate();
    }

}
