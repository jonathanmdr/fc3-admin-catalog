package org.fullcycle.admin.catalog.application.castmember.create;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            castMemberGateway
        );
    }

    @Test
    void givenAValidCommand_whenCallCreateCastMember_thenReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        when(castMemberGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertNotNull(actual.id());

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

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAnInvalidType_whenCallCreateCastMember_thenShouldReturnNotificationException() {
        final var expectedErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateCastMemberCommand.with("Vin Diesel", null);

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getErrors().get(0).message());

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAnInvalidNameAndInvalidType_whenCallCreateCastMember_thenShouldReturnNotificationException() {
        final var expectedFirstErrorMessage = "'name' should not be null";
        final var expectedSecondErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 2;

        final var command = CreateCastMemberCommand.with(null, null);

        final var actual = assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            "Failed to create a aggregate CastMember"
        );

        assertNotNull(actual);
        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedFirstErrorMessage, actual.getErrors().get(0).message());
        assertEquals(expectedSecondErrorMessage, actual.getErrors().get(1).message());

        verify(castMemberGateway, never()).create(any());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsUnexpectedException_thenShouldReturnAException() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorMessage = "Gateway unexpected error";

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        when(castMemberGateway.create(any()))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        verify(castMemberGateway, times(1))
            .create(argThat(castMember ->
                Objects.nonNull(castMember.getId())
                    && Objects.equals(expectedName, castMember.getName())
                    && Objects.equals(expectedType, castMember.getType())
                    && Objects.nonNull(castMember.getCreatedAt())
                    && Objects.nonNull(castMember.getUpdatedAt())
            ));
    }

}
