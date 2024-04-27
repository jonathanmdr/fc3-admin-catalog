package org.fullcycle.admin.catalog.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("COMPLETED")
public record VideoEncoderCompleted(
    String id,
    String outputBucketPath,
    @JsonProperty("video") VideoMetadata videoMetadata
) implements VideoEncoderResult {

    @Override
    public String getStatus() {
        return "COMPLETED";
    }

}
