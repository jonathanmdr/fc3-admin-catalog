package org.fullcycle.admin.catalog.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.GetGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.ListGenreResponse;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreRequest;
import org.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreResponse;
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

@Tag(name = "Genres")
@RequestMapping("/genres")
public interface GenreAPI {

    @Operation(summary = "List all genres paginated")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listed successfully"),
        @ApiResponse(responseCode = "422", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @GetMapping(
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    Pagination<ListGenreResponse> listGenres(
        @RequestParam(name = "search", required = false, defaultValue = "") final String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
        @RequestParam(name = "direction", required = false, defaultValue = "asc") final String direction
    );

    @Operation(summary = "Get a genre by it's identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Genre retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Genre was not found"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @GetMapping(
        value = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    GetGenreResponse getGenreById(@PathVariable(name = "id") final String id);

    @Operation(summary = "Create a new genre")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CreateGenreResponse> createGenre(@RequestBody CreateGenreRequest input);

    @Operation(summary = "Update a genre")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Genre updated successfully"),
        @ApiResponse(responseCode = "404", description = "Genre was not found"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UpdateGenreResponse> updateGenre(@PathVariable(name = "id") final String id, @RequestBody UpdateGenreRequest input);

    @Operation(summary = "Delete a genre by it's identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteGenreById(@PathVariable(name = "id") final String id);

}
