package org.fullcycle.admin.catalog.application.castmember.retrieve.get;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class GetCastMemberByIdUseCaseIT {

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private GetCastMemberByIdUseCase useCase;

    @Test
    void givenAValidId_whenCallsGetCastMemberById_thenReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var castMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = castMember.getId();

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        final var command = GetCastMemberByIdCommand.with(expectedId.getValue());

        final var actual = useCase.execute(command);

        assertThat(actual.id()).isEqualTo(expectedId);
        assertThat(actual.name()).isEqualTo(expectedName);
        assertThat(actual.type()).isEqualTo(expectedType);
        assertThat(actual.createdAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actual.updatedAt()).isEqualTo(castMember.getUpdatedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsGetCastMemberById_thenReturnNotFoundException() {
        final var expectedId = CastMemberID.unique();
        final var expectedErrorMessage = "CastMember with ID %s was not found".formatted(expectedId.getValue());

        final var command = GetCastMemberByIdCommand.with(expectedId.getValue());

        assertThat(castMemberRepository.count()).isZero();

        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command)
        );

        assertThat(castMemberRepository.count()).isZero();
        assertThat(actual.getMessage()).isEqualTo(expectedErrorMessage);

        verify(castMemberGateway, times(1)).findById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var expectedId = CastMemberID.unique();
        final var expectedCastMember = CastMember.newMember("Bla", CastMemberType.DIRECTOR);
        final var expectedErrorMessage = "Gateway unexpected error";

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.save(CastMemberJpaEntity.from(expectedCastMember));

        assertThat(castMemberRepository.count()).isOne();

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(castMemberGateway).findById(any());

        final var command = GetCastMemberByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertThat(actual.getMessage()).isEqualTo(expectedErrorMessage);
        verify(castMemberGateway, times(1)).findById(expectedId);
    }

}
