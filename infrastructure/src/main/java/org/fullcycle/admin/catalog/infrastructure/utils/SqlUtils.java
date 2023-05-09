package org.fullcycle.admin.catalog.infrastructure.utils;

import java.util.Objects;

public final class SqlUtils {

    private SqlUtils() { }

    public static String like(final String term) {
        if (Objects.isNull(term)) {
            return null;
        }

        return "%".concat(term.toUpperCase()).concat("%");
    }

}
