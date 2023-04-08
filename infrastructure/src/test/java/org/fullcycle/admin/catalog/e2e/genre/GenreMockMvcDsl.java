package org.fullcycle.admin.catalog.e2e.genre;

import org.fullcycle.admin.catalog.e2e.MockMvcDsl;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.GetGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

public abstract class GenreMockMvcDsl implements MockMvcDsl {

    public abstract MockMvc mvc();

    protected ResultActions listGenres(final int page, final int perPage)throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    protected ResultActions listGenres(final int page, final int perPage, final String search)throws Exception {
        return listGenres(page, perPage, search, "", "");
    }

    protected ResultActions listGenres(
        final int page,
        final int perPage,
        final String search,
        final String sort,
        final String direction
    ) throws Exception {
        return get("/genres", page, perPage, search, sort, direction);
    }

    protected GetGenreResponse retrieveAGenreById(final String id) throws Exception {
        final var response = get("/genres", id);
        return Json.readValue(response, GetGenreResponse.class);
    }

    protected void retrieveANotFoundGenreById(final String id) throws Exception {
        get("/genres", id, "Genre with ID %s was not found".formatted(id));
    }

    protected CreateGenreResponse givenAGenre(
        final String name,
        final List<String> categories,
        final boolean isActive
    ) throws Exception {
        final var createGenreRequest = new CreateGenreRequest(
            name,
            categories,
            isActive
        );

        final var response = post("/genres", createGenreRequest);

        return Json.readValue(response, CreateGenreResponse.class);
    }

    protected UpdateGenreResponse updateAGenre(
        final String id,
        final String name,
        final List<String> categories,
        final boolean active
    ) throws Exception {
        final var updateGenreRequest = new UpdateGenreRequest(
            name,
            categories,
            active
        );

        final var response = put("/genres", id, updateGenreRequest);

        return Json.readValue(response, UpdateGenreResponse.class);
    }

    protected void deleteAGenre(final String id) throws Exception {
        delete("/genres", id);
    }

}
