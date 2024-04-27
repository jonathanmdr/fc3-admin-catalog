package org.fullcycle.admin.catalog.infrastructure.category.models;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class UpdateCategoryResponseTest {

    @Autowired
    private JacksonTester<UpdateCategoryResponse> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedId = CategoryID.unique().getValue();

        final var response = new UpdateCategoryResponse(expectedId);

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.id", expectedId);
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedId = CategoryID.unique().getValue();

        final var json = """
            {
              "id": "%s"
            }
            """
            .formatted(expectedId);

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expectedId);
    }

}
