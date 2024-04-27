package org.fullcycle.admin.catalog.infrastructure.category.models;

import org.fullcycle.admin.catalog.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class UpdateCategoryRequestTest {

    @Autowired
    private JacksonTester<UpdateCategoryRequest> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var response = new UpdateCategoryRequest(
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.name", expectedName)
            .hasJsonPath("$.description", expectedDescription)
            .hasJsonPath("$.is_active", expectedIsActive);
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var json = """
            {
              "name": "%s",
              "description": "%s",
              "is_active": %s
            }
            """
            .formatted(
                expectedName,
                expectedDescription,
                expectedIsActive
            );

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("name", expectedName)
            .hasFieldOrPropertyWithValue("description", expectedDescription)
            .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }

}
