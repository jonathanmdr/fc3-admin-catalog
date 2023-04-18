package org.fullcycle.admin.catalog.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.GetCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.ListCastMembersResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberResponse;
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

@Tag(name = "Cast Members")
@RequestMapping("/cast-members")
public interface CastMemberAPI {

    @Operation(summary = "List all cast members paginated")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listed successfully"),
        @ApiResponse(responseCode = "422", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @GetMapping(
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    Pagination<ListCastMembersResponse> listCastMembers(
        @RequestParam(name = "search", required = false, defaultValue = "") final String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
        @RequestParam(name = "direction", required = false, defaultValue = "asc") final String direction
    );

    @Operation(summary = "Get a cast member by it's identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cast member retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Cast member was not found"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @GetMapping(
        value = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    GetCastMemberResponse getCastMemberById(@PathVariable(name = "id") final String id);

    @Operation(summary = "Create a new cast member")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CreateCastMemberResponse> createCastMember(@RequestBody CreateCastMemberRequest input);

    @Operation(summary = "Update a cast member")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cast member updated successfully"),
        @ApiResponse(responseCode = "404", description = "Cast member was not found"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UpdateCastMemberResponse> updateCastMember(@PathVariable(name = "id") final String id, @RequestBody UpdateCastMemberRequest input);

    @Operation(summary = "Delete a cast member by it's identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cast member deleted successfully"),
        @ApiResponse(responseCode = "500", description = "An unexpected internal server error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteCastMemberById(@PathVariable(name = "id") final String id);

}
