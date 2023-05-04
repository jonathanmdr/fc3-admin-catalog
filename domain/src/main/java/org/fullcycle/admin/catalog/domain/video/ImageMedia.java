package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.ValueObject;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;

import java.util.Objects;

public class ImageMedia extends ValueObject {

    private final String id;
    private final String name;
    private final String checksum;
    private final String location;

    private ImageMedia(
        final String id,
        final String name,
        final String checksum,
        final String location
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.checksum = Objects.requireNonNull(checksum);
        this.location = Objects.requireNonNull(location);
    }

    public static ImageMedia newImageMedia(
        final String name,
        final String checksum,
        final String location
    ) {
        return new ImageMedia(
            IdentifierUtils.unique(),
            name,
            checksum,
            location
        );
    }

    public static ImageMedia with(
        final String id,
        final String name,
        final String checksum,
        final String location
    ) {
        return new ImageMedia(
            id,
            name,
            checksum,
            location
        );
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String checksum() {
        return checksum;
    }

    public String location() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImageMedia that = (ImageMedia) o;
        return Objects.equals(checksum, that.checksum) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, location);
    }

}
