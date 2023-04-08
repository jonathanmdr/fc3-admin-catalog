package org.fullcycle.admin.catalog.infrastructure.models.genre;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.infrastructure.genre.models.ListGenreResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class ListGenreResponseTest {

    @Autowired
    private JacksonTester<ListGenreResponse> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedId = GenreID.unique().getValue();
        final var expectedName = "Movies";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();

        final var response = new ListGenreResponse(
            expectedId,
            expectedName,
            expectedIsActive,
            expectedCreatedAt,
            expectedDeletedAt
        );

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.id", expectedId)
            .hasJsonPath("$.name", expectedName)
            .hasJsonPath("$.is_active", expectedIsActive)
            .hasJsonPath("$.created_at", expectedCreatedAt.toString())
            .hasJsonPath("$.deleted_at", expectedDeletedAt.toString());
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedId = GenreID.unique().getValue();
        final var expectedName = "Movies";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();

        final var json = """
            {
              "id": "%s",
              "name": "%s",
              "is_active": %s,
              "created_at": "%s",
              "deleted_at": "%s"
            }
            """
            .formatted(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCreatedAt.toString(),
                expectedDeletedAt.toString()
            );

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expectedId)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("active", expectedIsActive)
            .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
            .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt);
    }

}
