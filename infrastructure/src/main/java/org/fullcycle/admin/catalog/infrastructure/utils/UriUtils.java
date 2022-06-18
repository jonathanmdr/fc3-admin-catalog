package org.fullcycle.admin.catalog.infrastructure.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public final class UriUtils {

    private UriUtils() { }

    public static URI buildAndExpandResourceId(final Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
    }

}
