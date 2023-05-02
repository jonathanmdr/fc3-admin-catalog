package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CastMemberRepository extends JpaRepository<VideoJpaEntity, UUID> {

    Page<VideoJpaEntity> findAll(final Specification<VideoJpaEntity> whereClause, final Pageable pageable);

}
