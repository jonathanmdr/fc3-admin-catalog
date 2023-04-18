package org.fullcycle.admin.catalog.infrastructure.models.castmember;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.GetCastMemberResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class GetCastMemberResponseTest {

    @Autowired
    private JacksonTester<GetCastMemberResponse> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedId = CastMemberID.unique().getValue();
        final var expectedName = "Vin Diesel";
        final var expectedType = "ACTOR";
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var response = new GetCastMemberResponse(
            expectedId,
            expectedName,
            expectedType,
            expectedCreatedAt,
            expectedUpdatedAt
        );

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.id", expectedId)
            .hasJsonPath("$.name", expectedName)
            .hasJsonPath("$.type", expectedType)
            .hasJsonPath("$.created_at", expectedCreatedAt.toString())
            .hasJsonPath("$.updated_at", expectedUpdatedAt.toString());
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedId = CastMemberID.unique().getValue();
        final var expectedName = "Vin Diesel";
        final var expectedType = "ACTOR";
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var json = """
            {
              "id": "%s",
              "name": "%s",
              "type": "%s",
              "created_at": "%s",
              "updated_at": "%s"
            }
            """
            .formatted(
                expectedId,
                expectedName,
                expectedType,
                expectedCreatedAt.toString(),
                expectedUpdatedAt.toString()
            );

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expectedId)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("type", expectedType)
            .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
            .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt);
    }

}
