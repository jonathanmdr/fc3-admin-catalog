package org.fullcycle.admin.catalog.e2e.castmember;

import org.fullcycle.admin.catalog.e2e.MockMvcDsl;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.GetCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public abstract class CastMemberMockMvcDsl implements MockMvcDsl {

    public abstract MockMvc mvc();

    protected ResultActions listCastMembers(final int page, final int perPage) throws Exception {
        return listCastMembers(page, perPage, "", "", "");
    }

    protected ResultActions listCastMembers(final int page, final int perPage, final String search) throws Exception {
        return listCastMembers(page, perPage, search, "", "");
    }

    protected ResultActions listCastMembers(
        final int page,
        final int perPage,
        final String search,
        final String sort,
        final String direction
    ) throws Exception {
        return get("/cast-members", page, perPage, search, sort, direction);
    }

    protected GetCastMemberResponse retrieveACastMemberById(final String id) throws Exception {
        final var response = get("/cast-members", id);
        return Json.readValue(response, GetCastMemberResponse.class);
    }

    protected void retrieveANotFoundCastMemberById(final String id) throws Exception {
        get("/cast-members", id, "CastMember with ID %s was not found".formatted(id));
    }

    protected CreateCastMemberResponse givenACastMember(
        final String name,
        final String type
    ) throws Exception {
        final var createCastMemberRequest = new CreateCastMemberRequest(
            name,
            type
        );

        final var response = post("/cast-members", createCastMemberRequest);

        return Json.readValue(response, CreateCastMemberResponse.class);
    }

    protected UpdateCastMemberResponse updateACastMember(
        final String id,
        final String name,
        final String type
    ) throws Exception {
        final var updateCastMemberRequest = new UpdateCastMemberRequest(
            name,
            type
        );

        final var response = put("/cast-members", id, updateCastMemberRequest);

        return Json.readValue(response, UpdateCastMemberResponse.class);
    }

    protected void deleteACastMember(final String id) throws Exception {
        delete("/cast-members", id);
    }

}
