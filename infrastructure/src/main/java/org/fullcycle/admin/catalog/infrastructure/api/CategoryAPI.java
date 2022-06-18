package org.fullcycle.admin.catalog.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import org.fullcycle.admin.catalog.infrastructure.category.models.GetCategoryApiOutput;
import org.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryApiInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Categories")
@RequestMapping(
    value = "/categories",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
public interface CategoryAPI {

    @Operation(summary = "List all categories paginated")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listed successfully"),
        @ApiResponse(responseCode = "422", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @GetMapping
    Pagination<?> listCategories(
        @RequestParam(name = "search", required = false, defaultValue = "") final String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
        @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction
    );

    @Operation(summary = "Get a category by it's identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category was not found"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @GetMapping("/{id}")
    GetCategoryApiOutput getCategoryById(@PathVariable(name = "id") final String id);

    @Operation(summary = "Create a new category")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @PostMapping
    ResponseEntity<?> createCategory(@RequestBody CreateCategoryApiInput input);

    @Operation(summary = "Update a category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category was not found"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @PutMapping("/{id}")
    ResponseEntity<?> updateCategory(@PathVariable(name = "id") final String id, @RequestBody UpdateCategoryApiInput input);

    @Operation(summary = "Delete a category by it's identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteCategoryById(@PathVariable(name = "id") final String id);

}
