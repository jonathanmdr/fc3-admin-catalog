package org.fullcycle.admin.catalog.infrastructure.video.models;

import org.fullcycle.admin.catalog.JacksonTest;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
class VideoEncoderResultTest {

    @Autowired
    private JacksonTester<VideoEncoderResult> jacksonTester;

    @Test
    void testUnmarshallSuccessResult() throws IOException {
        final var expectedId = IdentifierUtils.unique();
        final var expectedOutputBucketPath = "output-bucket-path";
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "encoder-video-folder";
        final var expectedResourceId = IdentifierUtils.unique();
        final var expectedFilePath = "file-path";

        final var json = """
            {
                "id": "%s",
                "output_bucket_path": "%s",
                "status": "%s",
                "video": {
                    "encoded_video_folder": "%s",
                    "resource_id": "%s",
                    "file_path": "%s"
                }
            }
        """.formatted(expectedId, expectedOutputBucketPath, expectedStatus, expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .isExactlyInstanceOf(VideoEncoderCompleted.class)
            .hasFieldOrPropertyWithValue("id", expectedId)
            .hasFieldOrPropertyWithValue("outputBucketPath", expectedOutputBucketPath)
            .hasFieldOrPropertyWithValue("videoMetadata.encodedVideoFolder", expectedEncoderVideoFolder)
            .hasFieldOrPropertyWithValue("videoMetadata.resourceId", expectedResourceId)
            .hasFieldOrPropertyWithValue("videoMetadata.filePath", expectedFilePath);
    }

}
