package org.fullcycle.admin.catalog.application.category.retrieve.list;

public record ListCategoriesCommand(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {

    public static ListCategoriesCommand with(
        final int page,
        final int perPage,
        final String terms,
        final String sort,
        final String direction
    ) {
        return new ListCategoriesCommand(
            page,
            perPage,
            terms,
            sort,
            direction
        );
    }

}
