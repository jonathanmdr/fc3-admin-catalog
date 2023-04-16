package org.fullcycle.admin.catalog.application.castmember.delete;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultDeleteCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            castMemberGateway
        );
    }

    @Test
    void givenAValidId_whenCallsDeleteCastMemberById_shouldBeOk() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedId = castMember.getId();

        doNothing().when(castMemberGateway).deleteById(expectedId);

        final var command = DeleteCastMemberCommand.with(expectedId.getValue());

        assertDoesNotThrow(() -> useCase.execute(command));

        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCastMemberById_shouldBeOk() {
        final var expectedId = CastMemberID.unique();

        doNothing().when(castMemberGateway).deleteById(expectedId);

        final var command = DeleteCastMemberCommand.with(expectedId.getValue());

        assertDoesNotThrow(() -> useCase.execute(command));

        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedId = castMember.getId();
        final var expectedErrorMessage = "Gateway unexpected error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(castMemberGateway).deleteById(expectedId);

        final var command = DeleteCastMemberCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

}
