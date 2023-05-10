package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.ValueObject;
import org.fullcycle.admin.catalog.domain.resource.Resource;

import java.util.Objects;

public class VideoResource extends ValueObject {

    private final Resource resource;
    private final MediaType type;

    private VideoResource(final Resource resource, final MediaType type) {
        this.resource = Objects.requireNonNull(resource);
        this.type = Objects.requireNonNull(type);
    }

    public static VideoResource with(final Resource resource, final MediaType type) {
        return new VideoResource(resource, type);
    }

    public Resource resource() {
        return resource;
    }

    public MediaType type() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoResource that = (VideoResource) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
