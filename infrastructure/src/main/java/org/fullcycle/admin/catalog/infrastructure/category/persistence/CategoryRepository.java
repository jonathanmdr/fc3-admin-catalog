package org.fullcycle.admin.catalog.infrastructure.category.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, String> {

}
