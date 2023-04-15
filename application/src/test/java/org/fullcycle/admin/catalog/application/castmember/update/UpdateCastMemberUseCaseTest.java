package org.fullcycle.admin.catalog.application.castmember.update;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

class UpdateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultUpdateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            castMemberGateway
        );
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_thenReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedCastMember = CastMember.newMember("Vin", CastMemberType.DIRECTOR);

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            expectedName,
            expectedType
        );

        when(castMemberGateway.findById(expectedCastMember.getId()))
            .thenReturn(Optional.of(expectedCastMember));
        when(castMemberGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command);

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(castMemberGateway, times(1)).findById(expectedCastMember.getId());
        verify(castMemberGateway, times(1))
            .update(argThat(castMember ->
                Objects.nonNull(castMember.getId())
                && Objects.equals(expectedCastMember.getId(), castMember.getId())
                && Objects.equals(expectedName, castMember.getName())
                && Objects.equals(expectedType, castMember.getType())
                && Objects.nonNull(castMember.getCreatedAt())
                && Objects.nonNull(castMember.getUpdatedAt())
                && expectedCastMember.getUpdatedAt().isAfter(castMember.getCreatedAt())
            ));
    }

    @Test
    void givenAnInvalidId_whenCallsUpdateCastMember_thenShouldReturnNotFoundException() {
        final var expectedId = CastMemberID.unique();
        final var expectedErrorMessage = "CastMember with ID %s was not found".formatted(
            expectedId.getValue()
        );
        final var expectedErrorCount = 0;

        final var command = UpdateCastMemberCommand.with(
            expectedId.getValue(),
            "Vin Diesel",
            CastMemberType.ACTOR
        );

        when(castMemberGateway.findById(expectedId))
            .thenReturn(Optional.empty());

        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

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

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            null,
            CastMemberType.ACTOR
        );

        when(castMemberGateway.findById(expectedCastMember.getId()))
            .thenReturn(Optional.of(expectedCastMember));

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

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            "Vin Diesel",
            null
        );

        when(castMemberGateway.findById(expectedCastMember.getId()))
            .thenReturn(Optional.of(expectedCastMember));

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

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            null,
            null
        );

        when(castMemberGateway.findById(expectedCastMember.getId()))
            .thenReturn(Optional.of(expectedCastMember));

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

        final var command = UpdateCastMemberCommand.with(
            expectedCastMember.getId().getValue(),
            expectedName,
            expectedType
        );

        when(castMemberGateway.findById(expectedCastMember.getId()))
            .thenReturn(Optional.of(expectedCastMember));
        when(castMemberGateway.update(any()))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

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
                && expectedCastMember.getUpdatedAt().isAfter(castMember.getCreatedAt())
            ));
    }

}
