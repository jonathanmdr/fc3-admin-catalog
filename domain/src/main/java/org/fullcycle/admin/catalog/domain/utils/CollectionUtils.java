package org.fullcycle.admin.catalog.domain.utils;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    private CollectionUtils() { }

    public static <IN, OUT> Set<OUT> mapTo(final Collection<IN> list, Function<IN, OUT> mapper) {
        return list.stream()
            .map(mapper)
            .collect(Collectors.toSet());
    }

}
