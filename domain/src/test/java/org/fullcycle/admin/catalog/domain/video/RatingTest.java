package org.fullcycle.admin.catalog.domain.video;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RatingTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "ER,ER",
            "L,L",
            "AGE_10,10",
            "AGE_12,12",
            "AGE_14,14",
            "AGE_16,16",
            "AGE_18,18"
        }
    )
    void validateLabels(final Rating rating, final String label) {
        assertEquals(label, rating.label());
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "ER",
            "L",
            "10",
            "12",
            "14",
            "16",
            "18",
        }
    )
    void validateOf(final String label) {
        assertTrue(Rating.of(label).isPresent());
    }

    @Test
    void givenAnInvalidRatingLabel_whenCallsOf_shouldReturnEmpty() {
        assertTrue(Rating.of("BLA").isEmpty());
    }

}
