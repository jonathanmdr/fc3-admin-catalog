package org.fullcycle.admin.catalog.infrastructure.genre.persistence;

import org.fullcycle.admin.catalog.DatabaseRepositoryIntegrationTest;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseRepositoryIntegrationTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void givenAnInvalidNullName_whenCallsSave_thenReturnError() {
        final var expectedPropertyName = "name";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity.name";

        final var genre = Genre.newGenre("Action", true);
        final var entity = GenreJpaEntity.from(genre);
        entity.setName(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> genreRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity.createdAt";

        final var genre = Genre.newGenre("Action", true);
        final var entity = GenreJpaEntity.from(genre);
        entity.setCreatedAt(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> genreRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity.updatedAt";

        final var genre = Genre.newGenre("Action", true);
        final var entity = GenreJpaEntity.from(genre);
        entity.setUpdatedAt(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> genreRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

}
