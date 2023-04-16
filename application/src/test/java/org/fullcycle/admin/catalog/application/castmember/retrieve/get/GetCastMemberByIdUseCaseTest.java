package org.fullcycle.admin.catalog.application.castmember.retrieve.get;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCastMemberByIdUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultGetCastMemberByIdUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            castMemberGateway
        );
    }

    @Test
    void givenAValidId_whenCallsGetCastMemberById_thenReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var castMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = castMember.getId();

        when(castMemberGateway.findById(expectedId))
            .thenReturn(Optional.of(CastMember.with(castMember)));

        final var command = GetCastMemberByIdCommand.with(expectedId.getValue());

        final var actual = useCase.execute(command);

        assertEquals(expectedId, actual.id());
        assertEquals(expectedName, actual.name());
        assertEquals(expectedType, actual.type());
        assertEquals(castMember.getCreatedAt(), actual.createdAt());
        assertEquals(castMember.getUpdatedAt(), actual.updatedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsGetCastMemberById_thenReturnNotFoundException() {
        final var expectedId = CastMemberID.unique();
        final var expectedErrorMessage = "CastMember with ID %s was not found".formatted(expectedId.getValue());

        when(castMemberGateway.findById(expectedId))
            .thenReturn(Optional.empty());

        final var command = GetCastMemberByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(castMemberGateway, times(1)).findById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var expectedId = CastMemberID.unique();
        final var expectedErrorMessage = "Gateway unexpected error";

        when(castMemberGateway.findById(expectedId))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var command = GetCastMemberByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(castMemberGateway, times(1)).findById(expectedId);
    }

}
