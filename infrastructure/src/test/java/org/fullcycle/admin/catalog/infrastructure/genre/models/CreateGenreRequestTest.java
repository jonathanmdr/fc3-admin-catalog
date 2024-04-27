package org.fullcycle.admin.catalog.infrastructure.genre.models;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class CreateGenreRequestTest {

    @Autowired
    private JacksonTester<CreateGenreRequest> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedName = "Movies";
        final var expectedCategories = List.of(
            CategoryID.unique().getValue()
        );
        final var expectedIsActive = true;

        final var response = new CreateGenreRequest(
            expectedName,
            expectedCategories,
            expectedIsActive
        );

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.name", expectedName)
            .hasJsonPath("$.categories_ids", expectedCategories)
            .hasJsonPath("$.is_active", expectedIsActive);
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = "Movies";
        final var expectedCategories = List.of(
            CategoryID.unique().getValue()
        );
        final var expectedIsActive = true;

        final var json = """
            {
              "name": "%s",
              "categories_ids": ["%s"],
              "is_active": %s
            }
            """
            .formatted(
                expectedName,
                expectedCategories.get(0),
                expectedIsActive
            );

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("categories", expectedCategories)
            .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }

}
