package org.fullcycle.admin.catalog.application.castmember.retrieve.list;

public record ListCastMembersCommand(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) {

    public static ListCastMembersCommand with(
        final int page,
        final int perPage,
        final String terms,
        final String sort,
        final String direction
    ) {
        return new ListCastMembersCommand(
            page,
            perPage,
            terms,
            sort,
            direction
        );
    }

}
