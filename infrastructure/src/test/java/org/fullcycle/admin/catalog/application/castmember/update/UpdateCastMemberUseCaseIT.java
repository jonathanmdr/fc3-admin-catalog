package org.fullcycle.admin.catalog.application.castmember.update;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class UpdateCastMemberUseCaseIT {

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private UpdateCastMemberUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_thenReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedCastMember = CastMember.newMember("Vin", CastMemberType.DIRECTOR);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(expectedCastMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            expectedName,
            expectedType
        );

        final var actual = useCase.execute(command);

        assertThat(actual).isNotNull();
        assertThat(actual.id()).isNotNull();

        verify(castMemberGateway, times(1)).findById(expectedCastMember.getId());
        verify(castMemberGateway, times(1))
            .update(argThat(castMember ->
                Objects.nonNull(castMember.getId())
                && Objects.equals(expectedCastMember.getId(), castMember.getId())
                && Objects.equals(expectedName, castMember.getName())
                && Objects.equals(expectedType, castMember.getType())
                && Objects.nonNull(castMember.getCreatedAt())
                && Objects.nonNull(castMember.getUpdatedAt())
                && castMember.getUpdatedAt().isAfter(castMember.getCreatedAt())
            ));
    }

    @Test
    void givenAnInvalidId_whenCallsUpdateCastMember_thenShouldReturnNotFoundException() {
        final var expectedId = CastMemberID.unique();
        final var expectedErrorMessage = "CastMember with ID %s was not found".formatted(
            expectedId.getValue()
        );
        final var expectedErrorCount = 0;

        assertThat(castMemberRepository.count()).isZero();

        final var command = UpdateCastMemberCommand.with(
            expectedId.getValue(),
            "Vin Diesel",
            CastMemberType.ACTOR
        );

        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(castMemberRepository.count()).isZero();
        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getMessage());

        verify(castMemberGateway, times(1)).findById(expectedId);
        verify(castMemberGateway, never()).update(any());
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCastMember_thenShouldReturnNotificationException() {
        final var expectedCastMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(expectedCastMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            null,
            CastMemberType.ACTOR
        );

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to update the aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(castMemberGateway, times(1)).findById(expectedCastMember.getId());
        verify(castMemberGateway, never()).update(any());
    }

    @Test
    void givenAnInvalidType_whenCallsUpdateCastMember_thenShouldReturnNotificationException() {
        final var expectedCastMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 1;

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(expectedCastMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            "Vin Diesel",
            null
        );

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to update the aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(castMemberGateway, times(1)).findById(expectedCastMember.getId());
        verify(castMemberGateway, never()).update(any());
    }

    @Test
    void givenAnInvalidNameAndInvalidType_whenCallsUpdateCastMember_thenShouldReturnNotificationException() {
        final var expectedCastMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedFirstErrorMessage = "'name' should not be null";
        final var expectedSecondErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 2;

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(expectedCastMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            null,
            null
        );

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to update the aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedFirstErrorMessage, actual.getErrors().get(0).message());
        assertEquals(expectedSecondErrorMessage, actual.getErrors().get(1).message());

        verify(castMemberGateway, times(1)).findById(expectedCastMember.getId());
        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsUnexpectedException_thenShouldReturnAException() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedCastMember = CastMember.newMember("Bla", CastMemberType.DIRECTOR);
        final var expectedErrorMessage = "Gateway unexpected error";

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(expectedCastMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            expectedName,
            expectedType
        );

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(castMemberGateway).update(any());

        assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        verify(castMemberGateway, times(1)).findById(expectedCastMember.getId());
        verify(castMemberGateway, times(1))
            .update(argThat(castMember ->
                Objects.nonNull(castMember.getId())
                && Objects.equals(expectedCastMember.getId(), castMember.getId())
                && Objects.equals(expectedName, castMember.getName())
                && Objects.equals(expectedType, castMember.getType())
                && Objects.nonNull(castMember.getCreatedAt())
                && Objects.nonNull(castMember.getUpdatedAt())
                && castMember.getUpdatedAt().isAfter(castMember.getCreatedAt())
            ));
    }

}
