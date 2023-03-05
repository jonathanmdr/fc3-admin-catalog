package org.fullcycle.admin.catalog.application.genre.update;

import java.util.List;

public record UpdateGenreCommand(
    String id,
    String name,
    boolean isActive,
    List<String> categories
) {

    public static UpdateGenreCommand with(
        final String id,
        final String name,
        final boolean isActive,
        final List<String> categories
    ) {
        return new UpdateGenreCommand(id, name, isActive, categories);
    }

}
