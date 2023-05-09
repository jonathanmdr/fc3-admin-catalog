package org.fullcycle.admin.catalog.infrastructure.castmember;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.fullcycle.admin.catalog.infrastructure.utils.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils.like;

@Component
public class CastMemberDatabaseGateway implements CastMemberGateway {

    private final CastMemberRepository castMemberRepository;

    public CastMemberDatabaseGateway(final CastMemberRepository castMemberRepository) {
        this.castMemberRepository = Objects.requireNonNull(castMemberRepository);
    }

    @Override
    public Pagination<CastMember> findAll(final SearchQuery query) {
        final var page = PageRequest.of(
            query.page(),
            query.perPage(),
            Sort.by(Direction.fromString(query.direction()), query.sort())
        );

        final var specifications = Optional.ofNullable(query.terms())
            .filter(StringUtils::isNotBlank)
            .map(CastMemberDatabaseGateway::applyTerms)
            .orElse(null);

        final var pageResult = this.castMemberRepository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.map(CastMemberJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public Optional<CastMember> findById(final CastMemberID castMemberID) {
        return this.castMemberRepository.findById(castMemberID.getValue())
            .map(CastMemberJpaEntity::toAggregate);
    }

    @Override
    public CastMember create(final CastMember castMember) {
        return save(castMember);
    }

    @Override
    public CastMember update(final CastMember castMember) {
        return save(castMember);
    }

    @Override
    public void deleteById(final CastMemberID castMemberID) {
        final var id = castMemberID.getValue();
        if (this.castMemberRepository.existsById(id)) {
            this.castMemberRepository.deleteById(id);
        }
    }

    @Override
    public List<CastMemberID> existsByIds(final Iterable<CastMemberID> castMemberIds) {
        final var ids = StreamSupport.stream(castMemberIds.spliterator(), false)
            .map(Identifier::getValue)
            .toList();

        return this.castMemberRepository.existsByIds(ids)
            .stream()
            .map(CastMemberID::from)
            .toList();
    }

    private static Specification<CastMemberJpaEntity> applyTerms(final String term) {
        return like("name", term);
    }

    private CastMember save(final CastMember castMember) {
        return this.castMemberRepository.save(
            CastMemberJpaEntity.from(castMember)
        ).toAggregate();
    }

}
