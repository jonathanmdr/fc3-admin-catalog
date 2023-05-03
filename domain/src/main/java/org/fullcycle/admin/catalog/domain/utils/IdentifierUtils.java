package org.fullcycle.admin.catalog.domain.utils;

import java.util.UUID;

public final class IdentifierUtils {

    private IdentifierUtils() { }

    public static String unique() {
        return formatId(UUID.randomUUID());
    }

    public static String from(final String uuid) {
        return sanitize(uuid);
    }

    private static String formatId(final UUID uuid) {
        return sanitize(uuid.toString());
    }

    private static String sanitize(final String uuid) {
        return uuid.toLowerCase()
            .replaceAll("-", "");
    }

}
