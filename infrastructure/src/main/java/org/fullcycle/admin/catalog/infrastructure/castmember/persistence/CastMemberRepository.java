package org.fullcycle.admin.catalog.infrastructure.castmember.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CastMemberRepository extends JpaRepository<CastMemberJpaEntity, String> {

    Page<CastMemberJpaEntity> findAll(final Specification<CastMemberJpaEntity> specification, final Pageable pageable);

    @Query(value = "select cm.id from CastMember cm where cm.id in :ids")
    List<String> existsByIds(@Param("ids") final List<String> ids);

}
