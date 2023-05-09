package org.fullcycle.admin.catalog.infrastructure.genre.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<GenreJpaEntity, String> {

    Page<GenreJpaEntity> findAll(final Specification<GenreJpaEntity> whereClause, final Pageable pageable);

    @Query(value = "select g.id from Genre g where g.id in :ids")
    List<String> existsByIds(@Param("ids") final List<String> ids);

}
