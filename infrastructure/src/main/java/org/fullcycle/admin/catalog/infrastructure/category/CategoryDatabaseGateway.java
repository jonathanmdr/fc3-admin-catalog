package org.fullcycle.admin.catalog.infrastructure.category;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
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
public class CategoryDatabaseGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryDatabaseGateway(final CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
    }

    @Override
    public Pagination<Category> findAll(final SearchQuery query) {
        final var page = PageRequest.of(
            query.page(),
            query.perPage(),
            Sort.by(Direction.fromString(query.direction()), query.sort())
        );

        final var specifications = Optional.ofNullable(query.terms())
            .filter(StringUtils::isNotBlank)
            .map(CategoryDatabaseGateway::applyTerms)
            .orElse(null);

        final var pageResult = this.categoryRepository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
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

    @Override
    public List<CategoryID> existsByIds(final Iterable<CategoryID> categoryIds) {
        final var ids = StreamSupport.stream(categoryIds.spliterator(), false)
            .map(CategoryID::getValue)
            .toList();

        return this.categoryRepository.existsByIds(ids)
            .stream()
            .map(CategoryID::from)
            .toList();
    }

    private static Specification<CategoryJpaEntity> applyTerms(final String term) {
        final Specification<CategoryJpaEntity> nameLike = like("name", term);
        final Specification<CategoryJpaEntity> descriptionLike = like("description", term);
        return nameLike.or(descriptionLike);
    }

    private Category save(final Category category) {
        return this.categoryRepository.save(CategoryJpaEntity.from(category)).toAggregate();
    }

}
