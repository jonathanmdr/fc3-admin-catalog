package org.fullcycle.admin.catalog.domain.video;

import java.util.Optional;
import java.util.stream.Stream;

public enum Rating {

    ER("ER"),
    L("L"),
    AGE_10("10"),
    AGE_12("12"),
    AGE_14("14"),
    AGE_16("16"),
    AGE_18("18");

    private final String label;

    Rating(final String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static Optional<Rating> of(final String value) {
        return Stream.of(values())
            .filter(rating -> rating.label.equalsIgnoreCase(value))
            .findFirst();
    }

}
