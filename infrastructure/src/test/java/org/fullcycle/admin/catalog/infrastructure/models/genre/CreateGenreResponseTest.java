package org.fullcycle.admin.catalog.infrastructure.models.genre;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class CreateGenreResponseTest {

    @Autowired
    private JacksonTester<CreateGenreResponse> jacksonTester;

    @Test
    void testMarshall() throws Exception {
        final var expectedId = GenreID.unique().getValue();

        final var response = new CreateGenreResponse(expectedId);

        final var actual = this.jacksonTester.write(response);

        assertThat(actual)
            .hasJsonPath("$.id", expectedId);
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedId = GenreID.unique().getValue();

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
