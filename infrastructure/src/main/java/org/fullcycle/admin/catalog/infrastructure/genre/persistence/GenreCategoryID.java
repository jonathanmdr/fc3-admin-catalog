package org.fullcycle.admin.catalog.infrastructure.genre.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GenreCategoryID implements Serializable {

    @Column(name = "genre_id", nullable = false)
    private String genreId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    public GenreCategoryID() { }

    private GenreCategoryID(final String genreId, final String categoryId) {
        this.genreId = genreId;
        this.categoryId = categoryId;
    }

    public static GenreCategoryID from(final String genreId, final String categoryId) {
        return new GenreCategoryID(genreId, categoryId);
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GenreCategoryID that = (GenreCategoryID) o;
        return genreId.equals(that.genreId) && categoryId.equals(that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreId, categoryId);
    }

}
