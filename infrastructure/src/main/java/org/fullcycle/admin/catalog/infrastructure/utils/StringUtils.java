package org.fullcycle.admin.catalog.infrastructure.utils;

public final class StringUtils {

    private StringUtils() { }

    public static boolean isNotBlank(final String value) {
        return !value.isBlank();
    }

}
