package org.fullcycle.admin.catalog.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCastMemberResponse(
    @JsonProperty("id") String id
) {

}
