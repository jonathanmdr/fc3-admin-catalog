package org.fullcycle.admin.catalog.infrastructure.castmember.models;

import org.fullcycle.admin.catalog.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class UpdateCastMemberRequestTest {

    @Autowired
    private JacksonTester<UpdateCastMemberRequest> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedName = "Vin Diesel";
        final var expectedType = "ACTOR";

        final var response = new UpdateCastMemberRequest(
            expectedName,
            expectedType
        );

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.name", expectedName)
            .hasJsonPath("$.type", expectedType);
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = "Vin Diesel";
        final var expectedType = "ACTOR";

        final var json = """
            {
              "name": "%s",
              "type": "%s"
            }
            """
            .formatted(
                expectedName,
                expectedType
            );

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("type", expectedType);
    }

}
