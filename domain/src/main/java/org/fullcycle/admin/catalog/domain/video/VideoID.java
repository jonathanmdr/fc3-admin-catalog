package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.Identifier;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;

import java.util.Objects;

public class VideoID extends Identifier {

    private final String value;

    private VideoID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VideoID unique() {
        return VideoID.from(
            IdentifierUtils.unique()
        );
    }

    public static VideoID from(final String unique) {
        return new VideoID(
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
        final VideoID that = (VideoID) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

}
