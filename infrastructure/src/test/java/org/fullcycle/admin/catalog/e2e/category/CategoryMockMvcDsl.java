package org.fullcycle.admin.catalog.e2e.category;

import org.fullcycle.admin.catalog.e2e.MockMvcDsl;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryRequest;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryResponse;
import org.fullcycle.admin.catalog.infrastructure.category.models.GetCategoryResponse;
import org.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryRequest;
import org.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryResponse;
import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public abstract class CategoryMockMvcDsl implements MockMvcDsl {

    public abstract MockMvc mvc();

    protected ResultActions listCategories(final int page, final int perPage)throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    protected ResultActions listCategories(final int page, final int perPage, final String search)throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    protected ResultActions listCategories(
        final int page,
        final int perPage,
        final String search,
        final String sort,
        final String direction
    ) throws Exception {
        return get("/categories", page, perPage, search, sort, direction);
    }

    protected GetCategoryResponse retrieveACategoryById(final String id) throws Exception {
        final var response = get("/categories", id);
        return Json.readValue(response, GetCategoryResponse.class);
    }

    protected void retrieveANotFoundCategoryById(final String id) throws Exception {
        get("/categories", id, "Category with ID %s was not found".formatted(id));
    }

    protected CreateCategoryResponse givenACategory(
        final String name,
        final String description,
        final boolean isActive
    ) throws Exception {
        final var createCategoryRequest = new CreateCategoryRequest(
            name,
            description,
            isActive
        );

        final var response = post("/categories", createCategoryRequest);

        return Json.readValue(response, CreateCategoryResponse.class);
    }

    protected UpdateCategoryResponse updateACategory(
        final String id,
        final String name,
        final String description,
        final boolean active
    ) throws Exception {
        final var updateCategoryRequest = new UpdateCategoryRequest(
            name,
            description,
            active
        );

        final var response = put("/categories", id, updateCategoryRequest);

        return Json.readValue(response, UpdateCategoryResponse.class);
    }

    protected void deleteACategory(final String id) throws Exception {
        delete("/categories", id);
    }

}
