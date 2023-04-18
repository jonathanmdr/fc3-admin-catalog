package org.fullcycle.admin.catalog.application.castmember.create;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class CreateCastMemberUseCaseIT {

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private CreateCastMemberUseCase useCase;

    @Test
    void givenAValidCommand_whenCallCreateCastMember_thenReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        assertThat(castMemberRepository.count()).isZero();

        final var actual = useCase.execute(command);

        assertThat(castMemberRepository.count()).isOne();

        assertThat(actual).isNotNull();
        assertThat(actual.id()).isNotNull();

        verify(castMemberGateway, times(1))
            .create(argThat(castMember ->
                Objects.nonNull(castMember.getId())
                && Objects.equals(expectedName, castMember.getName())
                && Objects.equals(expectedType, castMember.getType())
                && Objects.nonNull(castMember.getCreatedAt())
                && Objects.nonNull(castMember.getUpdatedAt())
            ));
    }

    @Test
    void givenAnInvalidName_whenCallCreateCastMember_thenShouldReturnNotificationException() {
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateCastMemberCommand.with(null, CastMemberType.ACTOR);

        assertThat(castMemberRepository.count()).isZero();

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to create a aggregate CastMember"
        );

        assertThat(castMemberRepository.count()).isZero();

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors().size()).isEqualTo(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAnInvalidType_whenCallCreateCastMember_thenShouldReturnNotificationException() {
        final var expectedErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateCastMemberCommand.with("Vin Diesel", null);

        assertThat(castMemberRepository.count()).isZero();

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to create a aggregate CastMember"
        );

        assertThat(castMemberRepository.count()).isZero();

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors().size()).isEqualTo(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAnInvalidNameAndInvalidType_whenCallCreateCastMember_thenShouldReturnNotificationException() {
        final var expectedFirstErrorMessage = "'name' should not be null";
        final var expectedSecondErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 2;

        final var command = CreateCastMemberCommand.with(null, null);

        assertThat(castMemberRepository.count()).isZero();

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to create a aggregate CastMember"
        );

        assertThat(castMemberRepository.count()).isZero();

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors().size()).isEqualTo(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedFirstErrorMessage);
        assertThat(actual.getErrors().get(1).message()).isEqualTo(expectedSecondErrorMessage);

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsUnexpectedException_thenShouldReturnAException() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorMessage = "Gateway unexpected error";

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(castMemberGateway).create(any());

        assertThat(castMemberRepository.count()).isZero();

        assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(castMemberRepository.count()).isZero();

        verify(castMemberGateway, times(1))
            .create(argThat(actualCastMember ->
                Objects.nonNull(actualCastMember.getId())
                    && Objects.equals(expectedName, actualCastMember.getName())
                    && Objects.equals(expectedType, actualCastMember.getType())
                    && Objects.nonNull(actualCastMember.getCreatedAt())
                    && Objects.nonNull(actualCastMember.getUpdatedAt())
            ));
    }

}
