package org.fullcycle.admin.catalog.infrastructure.category.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, String> {
    Page<CategoryJpaEntity> findAll(final Specification<CategoryJpaEntity> whereClause, final Pageable pageable);

}
