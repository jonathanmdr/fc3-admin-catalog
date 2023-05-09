package org.fullcycle.admin.catalog.infrastructure.castmember;

import org.fullcycle.admin.catalog.DatabaseGatewayIntegrationTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DatabaseGatewayIntegrationTest
class CastMemberDatabaseGatewayTest {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private CastMemberDatabaseGateway castMemberDatabaseGateway;

    @Test
    void givenAValidCastMember_whenCallsCreate_shouldReturnANewCastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var castMember = CastMember.newMember(expectedName, expectedType);

        assertThat(castMemberRepository.count()).isZero();

        final var actual = castMemberDatabaseGateway.create(castMember);

        assertThat(castMemberRepository.count()).isOne();

        assertThat(actual.getId()).isEqualTo(castMember.getId());
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getType()).isEqualTo(expectedType);
        assertThat(actual.getCreatedAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(castMember.getUpdatedAt());

        final var actualEntity = castMemberRepository.findById(castMember.getId().getValue())
                .orElseThrow(() -> new IllegalStateException("Expected CasMember cannot be null"));

        assertThat(actualEntity.getId()).isEqualTo(actual.getId().getValue());
        assertThat(actualEntity.getName()).isEqualTo(expectedName);
        assertThat(actualEntity.getType()).isEqualTo(expectedType);
        assertThat(actualEntity.getCreatedAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actualEntity.getUpdatedAt()).isEqualTo(castMember.getUpdatedAt());
    }

    @Test
    void givenAValidCastMember_whenCallsUpdate_shouldReturnUpdatedCastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;
        final var castMember = CastMember.newMember("Vin", CastMemberType.DIRECTOR);

        assertThat(castMemberRepository.count()).isZero();

        final var castMemberSaved = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(castMember));
        assertThat("Vin").isEqualTo(castMemberSaved.getName());
        assertThat(CastMemberType.DIRECTOR).isEqualTo(castMemberSaved.getType());
        assertThat(castMember.getCreatedAt()).isEqualTo(castMemberSaved.getCreatedAt());
        assertThat(castMember.getUpdatedAt()).isEqualTo(castMemberSaved.getUpdatedAt());

        assertThat(castMemberRepository.count()).isOne();

        final var castMemberToUpdate = CastMember.with(castMember).update(expectedName, expectedType);
        final var actual = castMemberDatabaseGateway.update(castMemberToUpdate);

        assertThat(castMemberRepository.count()).isOne();
        assertThat(actual.getId()).isEqualTo(castMember.getId());
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getType()).isEqualTo(expectedType);
        assertThat(actual.getCreatedAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(castMember.getUpdatedAt());

        final var actualEntity = castMemberRepository.findById(castMember.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected CasMember cannot be null"));

        assertThat(actualEntity.getId()).isEqualTo(actual.getId().getValue());
        assertThat(actualEntity.getName()).isEqualTo(expectedName);
        assertThat(actualEntity.getType()).isEqualTo(expectedType);
        assertThat(actualEntity.getCreatedAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(castMember.getUpdatedAt());
    }

    @Test
    void givenAPrePersistedCastMember_whenTryToDelete_shouldBeDeleteCastMember() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        castMemberDatabaseGateway.deleteById(castMember.getId());

        assertThat(castMemberRepository.count()).isZero();
    }

    @Test
    void givenACastMemberWithInvalidId_whenTryToDelete_shouldDoNothing() {
        assertThat(castMemberRepository.count()).isZero();

        castMemberDatabaseGateway.deleteById(CastMemberID.unique());

        assertThat(castMemberRepository.count()).isZero();
    }

    @Test
    void givenAValidCategoryId_whenCallsFindById_shouldReturnCategory() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var castMember = CastMember.newMember(expectedName, expectedType);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        final var actual = castMemberDatabaseGateway.findById(castMember.getId())
            .orElseThrow(() -> new IllegalStateException("Expected CasMember cannot be null"));

        assertThat(actual.getId()).isEqualTo(castMember.getId());
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getType()).isEqualTo(expectedType);
        assertThat(actual.getCreatedAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(castMember.getUpdatedAt());

        final var actualEntity = castMemberRepository.findById(castMember.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected CasMember cannot be null"));

        assertThat(actualEntity.getId()).isEqualTo(actual.getId().getValue());
        assertThat(actualEntity.getName()).isEqualTo(expectedName);
        assertThat(actualEntity.getType()).isEqualTo(expectedType);
        assertThat(actualEntity.getCreatedAt()).isEqualTo(castMember.getCreatedAt());
        assertThat(actualEntity.getUpdatedAt()).isEqualTo(castMember.getUpdatedAt());
    }

    @Test
    void givenAnInvalidCastMemberId_whenCallsFindById_shouldReturnEmpty() {
        final var castMember = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(castMember));

        assertThat(castMemberRepository.count()).isOne();

        final var actual = castMemberDatabaseGateway.findById(CastMemberID.unique());

        assertThat(castMemberRepository.count()).isOne();
        assertThat(actual).isEmpty();
    }

    @Test
    void givenPrePersistedCastMembers_whenCallsFindAll_shouldReturnPaginatedCastMembers() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var vinDiesel = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var willSmith = CastMember.newMember("Will Smith", CastMemberType.ACTOR);
        final var stevenSpielberg = CastMember.newMember("Steven Spielberg", CastMemberType.DIRECTOR);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAll(List.of(
            CastMemberJpaEntity.from(vinDiesel),
            CastMemberJpaEntity.from(willSmith),
            CastMemberJpaEntity.from(stevenSpielberg)
        ));

        assertThat(castMemberRepository.count()).isEqualTo(3);

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actual = castMemberDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedPerPage);
        assertThat(actual.items().get(0).getId().getValue()).isEqualTo(stevenSpielberg.getId().getValue());
    }

    @Test
    void givenEmptyCastMembersTable_whenCallsFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertThat(castMemberRepository.count()).isZero();

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actual = castMemberDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedTotal);
    }

    @Test
    void givenFollowPagination_whenCallsFindAllWithSecondPage_shouldReturnCastMembersSecondPage() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var vinDiesel = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var willSmith = CastMember.newMember("Will Smith", CastMemberType.ACTOR);
        final var stevenSpielberg = CastMember.newMember("Steven Spielberg", CastMemberType.DIRECTOR);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAll(List.of(
            CastMemberJpaEntity.from(vinDiesel),
            CastMemberJpaEntity.from(willSmith),
            CastMemberJpaEntity.from(stevenSpielberg)
        ));

        assertThat(castMemberRepository.count()).isEqualTo(3);

        var query = new SearchQuery(0, 1, "", "name", "asc");
        var actual = castMemberDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedPerPage);
        assertThat(actual.items().get(0).getId().getValue()).isEqualTo(stevenSpielberg.getId().getValue());

        expectedPage = 1;
        query = new SearchQuery(1, 1, "", "name", "asc");
        actual = castMemberDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedPerPage);
        assertThat(actual.items().get(0).getId().getValue()).isEqualTo(vinDiesel.getId().getValue());

        expectedPage = 2;
        query = new SearchQuery(2, 1, "", "name", "asc");
        actual = castMemberDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedPerPage);
        assertThat(actual.items().get(0).getId().getValue()).isEqualTo(willSmith.getId().getValue());
    }

    @Test
    void givenPrePersistedCastMembers_whenCallsFindAllWithTermsMatchesName_shouldReturnPaginatedCastMembers() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var vinDiesel = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var willSmith = CastMember.newMember("Will Smith", CastMemberType.ACTOR);
        final var stevenSpielberg = CastMember.newMember("Steven Spielberg", CastMemberType.DIRECTOR);

        assertThat(castMemberRepository.count()).isZero();

        castMemberRepository.saveAll(List.of(
            CastMemberJpaEntity.from(vinDiesel),
            CastMemberJpaEntity.from(willSmith),
            CastMemberJpaEntity.from(stevenSpielberg)
        ));

        assertThat(castMemberRepository.count()).isEqualTo(3);

        final var query = new SearchQuery(0, 1, "wi", "name", "asc");
        final var actual = castMemberDatabaseGateway.findAll(query);

        assertThat(actual.currentPage()).isEqualTo(expectedPage);
        assertThat(actual.perPage()).isEqualTo(expectedPerPage);
        assertThat(actual.total()).isEqualTo(expectedTotal);
        assertThat(actual.items().size()).isEqualTo(expectedPerPage);
        assertThat(actual.items().get(0).getId().getValue()).isEqualTo(willSmith.getId().getValue());
    }

    @Test
    void givenPrePersistedGenres_whenCallsExistsByIds_shouldReturnIds() {
        final var vinDiesel = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        final var willSmith = CastMember.newMember("Will Smith", CastMemberType.ACTOR);
        final var theRock = CastMember.newMember("The Rock", CastMemberType.ACTOR);

        assertEquals(0, this.castMemberRepository.count());

        final var ids = this.castMemberRepository.saveAll(
                List.of(
                    CastMemberJpaEntity.from(vinDiesel),
                    CastMemberJpaEntity.from(willSmith),
                    CastMemberJpaEntity.from(theRock)
                )
            )
            .stream()
            .map(CastMemberJpaEntity::getId)
            .map(CastMemberID::from)
            .toList();

        assertEquals(3, this.castMemberRepository.count());

        final var actual = this.castMemberDatabaseGateway.existsByIds(ids);

        assertThat(actual).hasSize(3);
        assertThat(actual).containsAll(ids);
    }

}