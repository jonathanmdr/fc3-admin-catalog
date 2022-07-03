package org.fullcycle.admin.catalog.application.genre.create;

import java.util.List;
import java.util.Objects;

public record CreateGenreCommand(
    String name,
    boolean isActive,
    List<String> categories
) {

    public static CreateGenreCommand with(final String name, final Boolean isActive, final List<String> categories) {
        return new CreateGenreCommand(
            name,
            Objects.isNull(isActive) || isActive,
            categories
        );
    }

}
