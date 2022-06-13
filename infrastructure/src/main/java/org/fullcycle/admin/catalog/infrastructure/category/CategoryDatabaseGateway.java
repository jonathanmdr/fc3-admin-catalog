package org.fullcycle.admin.catalog.infrastructure.category;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryDatabaseGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryDatabaseGateway(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery categorySearchQuery) {
        return null;
    }

    @Override
    public Optional<Category> findById(final CategoryID categoryID) {
        return this.categoryRepository.findById(categoryID.getValue())
            .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category create(final Category category) {
        return save(category);
    }

    @Override
    public Category update(final Category category) {
        return save(category);
    }

    @Override
    public void deleteById(final CategoryID categoryID) {
        final var id = categoryID.getValue();
        if (this.categoryRepository.existsById(id)) {
            this.categoryRepository.deleteById(id);
        }
    }

    private Category save(final Category category) {
        return this.categoryRepository.save(CategoryJpaEntity.from(category)).toAggregate();
    }

}
