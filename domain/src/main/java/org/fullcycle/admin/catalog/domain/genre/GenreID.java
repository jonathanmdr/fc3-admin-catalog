package org.fullcycle.admin.catalog.domain.genre;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;

import java.util.Objects;

public class GenreID extends Identifier {

    private final String value;

    private GenreID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static GenreID unique() {
        return GenreID.from(
            IdentifierUtils.unique()
        );
    }

    public static GenreID from(final String unique) {
        return new GenreID(
            IdentifierUtils.from(unique)
        );
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GenreID that = (GenreID) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

}
