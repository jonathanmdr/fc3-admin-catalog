package org.fullcycle.admin.catalog.infrastructure.genre.models;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class GetGenreResponseTest {

    @Autowired
    private JacksonTester<GetGenreResponse> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedId = GenreID.unique().getValue();
        final var expectedName = "Movies";
        final var expectedCategories = List.of(
            CategoryID.unique().getValue(),
            CategoryID.unique().getValue()
        );
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();

        final var response = new GetGenreResponse(
            expectedId,
            expectedName,
            expectedCategories,
            expectedIsActive,
            expectedCreatedAt,
            expectedUpdatedAt,
            expectedDeletedAt
        );

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.id", expectedId)
            .hasJsonPath("$.name", expectedName)
            .hasJsonPath("$.categories_ids", expectedCategories)
            .hasJsonPath("$.is_active", expectedIsActive)
            .hasJsonPath("$.created_at", expectedCreatedAt.toString())
            .hasJsonPath("$.updated_at", expectedUpdatedAt.toString())
            .hasJsonPath("$.deleted_at", expectedDeletedAt.toString());
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedId = CategoryID.unique().getValue();
        final var expectedName = "Movies";
        final var expectedCategories = List.of(
            CategoryID.unique().getValue(),
            CategoryID.unique().getValue()
        );
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();

        final var json = """
            {
              "id": "%s",
              "name": "%s",
              "categories_ids": ["%s","%s"],
              "is_active": %s,
              "created_at": "%s",
              "updated_at": "%s",
              "deleted_at": "%s"
            }
            """
            .formatted(
                expectedId,
                expectedName,
                expectedCategories.get(0),
                expectedCategories.get(1),
                expectedIsActive,
                expectedCreatedAt.toString(),
                expectedUpdatedAt.toString(),
                expectedDeletedAt.toString()
            );

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expectedId)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("categories", expectedCategories)
            .hasFieldOrPropertyWithValue("active", expectedIsActive)
            .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
            .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt)
            .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt);
    }

}
