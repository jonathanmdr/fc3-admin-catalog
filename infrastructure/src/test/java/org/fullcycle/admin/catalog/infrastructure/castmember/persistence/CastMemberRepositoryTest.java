package org.fullcycle.admin.catalog.infrastructure.castmember.persistence;

import org.fullcycle.admin.catalog.DatabaseRepositoryIntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseRepositoryIntegrationTest
class CastMemberRepositoryTest {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    void givenAnInvalidNullName_whenCallsSave_thenReturnError() {
        final var expectedPropertyName = "name";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity.name";

        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var entity = CastMemberJpaEntity.from(castMember);
        entity.setName(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> castMemberRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity.createdAt";

        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var entity = CastMemberJpaEntity.from(castMember);
        entity.setCreatedAt(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> castMemberRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_thenReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessageError = "not-null property references a null or transient value : org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity.updatedAt";

        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var entity = CastMemberJpaEntity.from(castMember);
        entity.setUpdatedAt(null);

        var actual = assertThrows(DataIntegrityViolationException.class, () -> castMemberRepository.save(entity));
        var cause = assertInstanceOf(PropertyValueException.class, actual.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedMessageError, actual.getMessage());
    }

}
