package org.fullcycle.admin.catalog.e2e.castmember;

import org.fullcycle.admin.catalog.E2ETest;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@E2ETest
@Testcontainers
class CastMemberE2ETest extends CastMemberMockMvcDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
        .withDatabaseName("admin_catalog_videos")
        .withUsername("root")
        .withPassword("123456");

    @DynamicPropertySource
    public static void setDataSourceProperties(final DynamicPropertyRegistry registry) {
        final var privatePort = 3306;
        final var exposedPort = MYSQL_CONTAINER.getMappedPort(privatePort);
        System.out.printf("MariaDB container is running on port %s\n", exposedPort);
        registry.add("mariadb.port", () -> exposedPort);
    }

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewCastMemberWithValidValues() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        final var expectedName = "Will Smith";
        final var expectedType = "ACTOR";

        final var output = givenACastMember(expectedName, expectedType);
        final var actual = retrieveACastMemberById(output.id());

        assertThat(actual.id()).isEqualTo(output.id());
        assertThat(actual.name()).isEqualTo(expectedName);
        assertThat(actual.type()).isEqualTo(expectedType);
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();

        assertThat(castMemberRepository.count()).isOne();
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateToAllCastMembers() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        final var willSmith = givenACastMember("Will Smith", "ACTOR");
        final var vinDiesel = givenACastMember("Vin Diesel", "ACTOR");
        final var stevenSpielberg = givenACastMember("Steven Spielberg", "DIRECTOR");

        listCastMembers(0, 1)
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(stevenSpielberg.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Steven Spielberg")))
            .andExpect(jsonPath("$.items[0].type", equalTo("DIRECTOR")))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()));

        listCastMembers(1, 1)
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(vinDiesel.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")))
            .andExpect(jsonPath("$.items[0].type", equalTo("ACTOR")))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()));

        listCastMembers(2, 1)
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(willSmith.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Will Smith")))
            .andExpect(jsonPath("$.items[0].type", equalTo("ACTOR")))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()));

        listCastMembers(3, 1)
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));

        assertThat(castMemberRepository.count()).isEqualTo(3);
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchBetweenAllCastMembers() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        givenACastMember("Will Smith", "ACTOR");
        givenACastMember("Vin Diesel", "ACTOR");
        final var stevenSpielberg = givenACastMember("Steven Spielberg", "DIRECTOR");

        listCastMembers(0, 1, "spi")
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].id", equalTo(stevenSpielberg.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Steven Spielberg")))
            .andExpect(jsonPath("$.items[0].type", equalTo("DIRECTOR")))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()));

        assertThat(castMemberRepository.count()).isEqualTo(3);
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllCastMembersByNameDesc() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        final var willSmith = givenACastMember("Will Smith", "ACTOR");
        final var vinDiesel = givenACastMember("Vin Diesel", "ACTOR");
        final var stevenSpielberg = givenACastMember("Steven Spielberg", "DIRECTOR");

        listCastMembers(0, 3, "", "name", "desc")
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].id", equalTo(willSmith.id())))
            .andExpect(jsonPath("$.items[0].name", equalTo("Will Smith")))
            .andExpect(jsonPath("$.items[0].type", equalTo("ACTOR")))
            .andExpect(jsonPath("$.items[0].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[1].id", equalTo(vinDiesel.id())))
            .andExpect(jsonPath("$.items[1].name", equalTo("Vin Diesel")))
            .andExpect(jsonPath("$.items[1].type", equalTo("ACTOR")))
            .andExpect(jsonPath("$.items[1].created_at", IsNull.notNullValue()))
            .andExpect(jsonPath("$.items[2].id", equalTo(stevenSpielberg.id())))
            .andExpect(jsonPath("$.items[2].name", equalTo("Steven Spielberg")))
            .andExpect(jsonPath("$.items[2].type", equalTo("DIRECTOR")))
            .andExpect(jsonPath("$.items[2].created_at", IsNull.notNullValue()));

        assertThat(castMemberRepository.count()).isEqualTo(3);
    }

    @Test
    void asACatalogAdminIShouldBeAbleToToGetAGenreByIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        final var willSmith = givenACastMember("Will Smith", "ACTOR");
        final var actual = retrieveACastMemberById(willSmith.id());
        final var expected = castMemberRepository.findById(willSmith.id())
            .orElseThrow(() -> new IllegalStateException("Expected cast member cannot be null"));

        assertThat(actual.id()).isEqualTo(expected.getId());
        assertThat(actual.name()).isEqualTo("Will Smith");
        assertThat(actual.type()).isEqualTo("ACTOR");
        assertThat(actual.createdAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.updatedAt()).isEqualTo(expected.getUpdatedAt());

        assertThat(castMemberRepository.count()).isOne();
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCastMember() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        retrieveANotFoundCastMemberById(CastMemberID.unique().getValue());

        assertThat(castMemberRepository.count()).isZero();
    }

    @Test
    void asACatalogAdminIShouldBeAbleToUpdateACastMemberByItsIdentifier() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        final var createdCastMember = givenACastMember("Vin", "DIRECTOR");
        var actual = retrieveACastMemberById(createdCastMember.id());

        assertThat(actual.id()).isEqualTo(createdCastMember.id());
        assertThat(actual.name()).isEqualTo("Vin");
        assertThat(actual.type()).isEqualTo("DIRECTOR");
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();

        assertThat(castMemberRepository.count()).isOne();

        final var updatedGenre = updateACastMember(
            createdCastMember.id(),
            "Vin Diesel",
            "ACTOR"
        );
        actual = retrieveACastMemberById(createdCastMember.id());

        assertThat(actual.id()).isEqualTo(updatedGenre.id());
        assertThat(actual.name()).isEqualTo("Vin Diesel");
        assertThat(actual.type()).isEqualTo("ACTOR");
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();

        assertThat(castMemberRepository.count()).isOne();
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteACastMember() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        final var createdCastMember = givenACastMember("Vin Diesel", "ACTOR");

        assertThat(castMemberRepository.count()).isOne();

        deleteACastMember(createdCastMember.id());

        assertThat(castMemberRepository.count()).isZero();
    }

    @Test
    void asACatalogAdminIShouldBeAbleToDeleteANotFoundCastMember() throws Exception {
        assertTrue(MYSQL_CONTAINER.isRunning());

        assertThat(castMemberRepository.count()).isZero();

        deleteACastMember(CastMemberID.unique().getValue());

        assertThat(castMemberRepository.count()).isZero();
    }

}
