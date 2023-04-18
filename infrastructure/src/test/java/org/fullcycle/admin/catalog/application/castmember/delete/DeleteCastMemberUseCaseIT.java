package org.fullcycle.admin.catalog.application.castmember.delete;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class DeleteCastMemberUseCaseIT {

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private DeleteCastMemberUseCase useCase;

    @Test
    void givenAValidId_whenCallsDeleteCastMemberById_shouldBeOk() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedId = castMember.getId();

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = DeleteCastMemberCommand.with(expectedId.getValue());

        assertDoesNotThrow(() -> useCase.execute(command));

        assertThat(castMemberRepository.count()).isZero();
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCastMemberById_shouldBeOk() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedId = CastMemberID.unique();

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = DeleteCastMemberCommand.with(expectedId.getValue());

        assertDoesNotThrow(() -> useCase.execute(command));

        assertThat(castMemberRepository.count()).isOne();
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var expectedId = castMember.getId();
        final var expectedErrorMessage = "Gateway unexpected error";

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(castMemberGateway).deleteById(expectedId);

        final var command = DeleteCastMemberCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(castMemberRepository.count()).isOne();
        assertThat(actual.getMessage()).isEqualTo(expectedErrorMessage);
        verify(castMemberGateway, times(1)).deleteById(expectedId);
    }

}
