package org.fullcycle.admin.catalog.domain.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    private CollectionUtils() { }

    public static <IN, OUT> Set<OUT> mapTo(final Collection<IN> collection, Function<IN, OUT> mapper) {
        if (isNullOrEmpty(collection)) {
            return new HashSet<>();
        }

        return collection.stream()
            .map(mapper)
            .collect(Collectors.toSet());
    }

    public static <IN> Set<IN> nullIfEmpty(final Collection<IN> collection) {
        return isNullOrEmpty(collection) ? null : new HashSet<>(collection);
    }

    private static boolean isNullOrEmpty(final Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

}
