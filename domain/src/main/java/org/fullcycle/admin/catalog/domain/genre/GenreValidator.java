package org.fullcycle.admin.catalog.domain.genre;

import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.Validator;

import java.util.Objects;

public class GenreValidator extends Validator {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 255;

    private final Genre genre;

    public GenreValidator(final Genre genre, final ValidationHandler validationHandler) {
        super(validationHandler);
        this.genre = genre;
    }

    @Override
    public void validate() {
        checkNameConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.genre.getName();

        if (Objects.isNull(name)) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }

        if (name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be empty"));
            return;
        }

        final var nameLength = name.trim().length();
        if (nameLength < NAME_MIN_LENGTH || nameLength > NAME_MAX_LENGTH) {
            this.validationHandler().append(
                new Error("'name' must be between %s and %s characters".formatted(NAME_MIN_LENGTH, NAME_MAX_LENGTH))
            );
        }
    }

}
