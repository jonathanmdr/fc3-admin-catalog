package org.fullcycle.admin.catalog.infrastructure.genre.persistence;

import org.fullcycle.admin.catalog.domain.category.CategoryID;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "genres_categories")
public class GenreCategoryJpaEntity {

    @EmbeddedId
    private GenreCategoryID id;

    @ManyToOne
    @MapsId("genreId")
    private GenreJpaEntity genre;

    public GenreCategoryJpaEntity() { }

    private GenreCategoryJpaEntity(final GenreJpaEntity genre, final CategoryID categoryID) {
        this.id = GenreCategoryID.from(genre.getId(), categoryID.getValue());
        this.genre = genre;
    }

    public static GenreCategoryJpaEntity from(final GenreJpaEntity genre, final CategoryID categoryID) {
        return new GenreCategoryJpaEntity(genre, categoryID);
    }

    public GenreCategoryID getId() {
        return id;
    }

    public void setId(GenreCategoryID id) {
        this.id = id;
    }

    public GenreJpaEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreJpaEntity genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GenreCategoryJpaEntity that = (GenreCategoryJpaEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
