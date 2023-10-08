package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.video.Rating;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Optional;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(final Rating rating) {
        return Optional.ofNullable(rating)
            .map(Rating::label)
            .orElse(null);
    }

    @Override
    public Rating convertToEntityAttribute(final String ratingValue) {
        return Rating.of(ratingValue)
            .orElse(null);
    }

}
