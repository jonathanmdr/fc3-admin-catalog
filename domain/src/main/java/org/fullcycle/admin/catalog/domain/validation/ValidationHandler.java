package org.fullcycle.admin.catalog.domain.validation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ValidationHandler {

    ValidationHandler append(final Error error);
    ValidationHandler append(final ValidationHandler handler);
    <T> T validate(final Validation<T> validation);

    default List<Error> getErrors() {
        return Collections.emptyList();
    }

    default boolean hasErrors() {
        return Objects.nonNull(getErrors()) && !getErrors().isEmpty();
    }

    default Optional<Error> firstError() {
        if (!hasErrors()) {
            return Optional.empty();
        }

        return Optional.of(getErrors().get(0));
    }

    interface Validation<T> {
        T validate();
    }

}
