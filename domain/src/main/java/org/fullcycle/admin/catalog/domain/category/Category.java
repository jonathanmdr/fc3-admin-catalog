package org.fullcycle.admin.catalog.domain.category;

import org.fullcycle.admin.catalog.domain.AggregateRoot;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Objects;

public class Category extends AggregateRoot<CategoryID> {
    private String name;
    private String description;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Category(
        final CategoryID id,
        final String name,
        final String description,
        final boolean active,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        super(id);
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' should not be null");
        this.deletedAt = deletedAt;
    }

    public static Category newCategory(final String name, final String description, final boolean active) {
        final var id = CategoryID.unique();
        final var now = Instant.now();
        final var deletedAt = active ? null : now;
        return new Category(id, name, description, active, now, now, deletedAt);
    }

    public static Category with(final Category category) {
        return with(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.isActive(),
            category.getCreatedAt(),
            category.getUpdatedAt(),
            category.getDeletedAt()
        );
    }

    public static Category with(
        final CategoryID id,
        final String name,
        final String description,
        final boolean isActive,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        return new Category(id, name, description, isActive, createdAt, updatedAt, deletedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CategoryValidator(this, handler).validate();
    }

    public Category activate() {
        this.active = true;
        this.updatedAt = Instant.now();
        this.deletedAt = null;
        return this;
    }

    public Category deactivate() {
        if (Objects.isNull(getDeletedAt())) {
            this.deletedAt = Instant.now();
        }

        this.active = false;
        this.updatedAt = Instant.now();
        return this;
    }

    public Category update(final String name, final String description, final boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.updatedAt = Instant.now();

        if (active) {
            activate();
        } else {
            deactivate();
        }

        return this;
    }

    @Override
    public CategoryID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}