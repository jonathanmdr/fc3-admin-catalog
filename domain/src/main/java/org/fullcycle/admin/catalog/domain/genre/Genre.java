package org.fullcycle.admin.catalog.domain.genre;

import org.fullcycle.admin.catalog.domain.AggregateRoot;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.handler.NotificationHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(
        final GenreID genreID,
        final String name,
        final boolean active,
        final List<CategoryID> categories,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        super(genreID);
        this.name = name;
        this.active = active;
        this.categories = categories;
        this.createdAt = Objects.requireNonNull(createdAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updatedAt' should not be null");
        this.deletedAt = deletedAt;

        selfValidate();
    }

    public static Genre newGenre(final String name, final boolean isActive) {
        final var id = GenreID.unique();
        final var now = Instant.now();
        final var deletedAt = isActive ? null : now;
        return new Genre(id, name, isActive, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre with(
        final Genre genre
    ) {
        return with(
            genre.getId(),
            genre.getName(),
            genre.isActive(),
            new ArrayList<>(genre.getCategories()),
            genre.getCreatedAt(),
            genre.getUpdatedAt(),
            genre.getDeletedAt()
        );
    }

    public static Genre with(
        final GenreID id,
        final String name,
        final boolean isActive,
        final List<CategoryID> categories,
        final Instant createdAt,
        final Instant updatedAt,
        final Instant deletedAt
    ) {
        return new Genre(id, name, isActive, categories, createdAt, updatedAt, deletedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public Genre activate() {
        this.active = true;
        this.updatedAt = Instant.now();
        this.deletedAt = null;
        return this;
    }

    public Genre deactivate() {
        if (Objects.isNull(getDeletedAt())) {
            this.deletedAt = Instant.now();
        }

        this.active = false;
        this.updatedAt = Instant.now();
        return this;
    }

    public Genre update(final String name, final boolean active, final List<CategoryID> categories) {
        this.name = name;
        this.active = active;
        this.categories = new ArrayList<>(Objects.nonNull(categories) ? categories : Collections.emptyList());
        this.updatedAt = Instant.now();

        if (active) {
            activate();
        } else {
            deactivate();
        }

        selfValidate();

        return this;
    }

    public Genre addCategory(final CategoryID categoryID) {
        if (Objects.isNull(categoryID)) {
            return this;
        }

        this.categories.add(categoryID);
        this.updatedAt = Instant.now();

        return this;
    }

    public Genre addCategories(final List<CategoryID> categoryIds) {
        if (Objects.isNull(categoryIds) || categoryIds.isEmpty()) {
            return this;
        }

        this.categories.addAll(categoryIds);
        this.updatedAt = Instant.now();

        return this;
    }

    public Genre removeCategory(final CategoryID categoryID) {
        if (Objects.isNull(categoryID)) {
            return this;
        }

        this.categories.remove(categoryID);
        this.updatedAt = Instant.now();

        return this;
    }

    public Genre removeCategories(final List<CategoryID> categoryIds) {
        if (Objects.isNull(categoryIds) || categoryIds.isEmpty()) {
            return this;
        }

        this.categories.removeAll(categoryIds);
        this.updatedAt = Instant.now();

        return this;
    }

    private void selfValidate() {
        final var notification = NotificationHandler.create();
        validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("The aggregate genre validation failed", notification);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
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

}
