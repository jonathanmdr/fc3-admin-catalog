package org.fullcycle.admin.catalog.domain.video;

import org.fullcycle.admin.catalog.domain.validation.Error;
import org.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import org.fullcycle.admin.catalog.domain.validation.Validator;

import java.util.Objects;

public class VideoValidator extends Validator {

    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 255;
    private static final int DESCRIPTION_MIN_LENGTH = 1;
    private static final int DESCRIPTION_MAX_LENGTH = 4000;

    private final Video video;

    public VideoValidator(final Video video, final ValidationHandler validationHandler) {
        super(validationHandler);
        this.video = video;
    }

    @Override
    public void validate() {
        checkTitleConstraints();
        checkDescriptionConstraints();
        checkLaunchedAtConstraints();
        checkRatingConstraints();
    }

    private void checkTitleConstraints() {
        final var title = this.video.getTitle();

        if (Objects.isNull(title)) {
            this.validationHandler().append(new Error("'title' should not be null"));
            return;
        }

        if (title.isBlank()) {
            this.validationHandler().append(new Error("'title' should not be empty"));
            return;
        }

        final var nameLength = title.trim().length();
        if (nameLength < TITLE_MIN_LENGTH || nameLength > TITLE_MAX_LENGTH) {
            this.validationHandler().append(
                new Error("'title' must be between %s and %s characters".formatted(TITLE_MIN_LENGTH, TITLE_MAX_LENGTH))
            );
        }
    }

    private void checkDescriptionConstraints() {
        final var description = this.video.getDescription();

        if (Objects.isNull(description)) {
            this.validationHandler().append(new Error("'description' should not be null"));
            return;
        }

        if (description.isBlank()) {
            this.validationHandler().append(new Error("'description' should not be empty"));
            return;
        }

        final var nameLength = description.trim().length();
        if (nameLength < DESCRIPTION_MIN_LENGTH || nameLength > DESCRIPTION_MAX_LENGTH) {
            this.validationHandler().append(
                new Error("'description' must be between %s and %s characters".formatted(DESCRIPTION_MIN_LENGTH, DESCRIPTION_MAX_LENGTH))
            );
        }
    }

    private void checkLaunchedAtConstraints() {
        final var launchedAt = this.video.getLaunchedAt();

        if (Objects.isNull(launchedAt)) {
            this.validationHandler().append(new Error("'launchedAt' should not be null"));
        }
    }

    private void checkRatingConstraints() {
        final var rating = this.video.getRating();

        if (Objects.isNull(rating)) {
            this.validationHandler().append(new Error("'rating' should not be null"));
        }
    }

}
