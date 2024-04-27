package org.fullcycle.admin.catalog.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ERROR")
public record VideoEncoderError(
    VideoMessage message,
    String error
) implements VideoEncoderResult {

    @Override
    public String getStatus() {
        return "ERROR";
    }

}
