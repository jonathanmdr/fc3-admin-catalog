package org.fullcycle.admin.catalog.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCastMemberRequest(
    @JsonProperty("name") String name,
    @JsonProperty("type") String type
) {

}
