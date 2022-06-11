package org.fullcycle.admin.catalog.domain.validation;

import java.util.List;
import java.util.Objects;

public interface ValidationHandler {

    ValidationHandler append(final Error error);
    ValidationHandler append(final ValidationHandler handler);
    ValidationHandler validate(final Validation validation);
    List<Error> getErrors();

    default boolean hasErrors() {
        return Objects.nonNull(getErrors()) && !getErrors().isEmpty();
    }

    interface Validation {
        void validate();
    }

}
