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

    @Test
    void testMarshallSuccessResult() throws IOException {
        final var expectedId = IdentifierUtils.unique();
        final var expectedOutputBucket = "output-bucket-path";
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "encoder-video-folder";
        final var expectedResourceId = IdentifierUtils.unique();
        final var expectedFilePath = "file-path";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);
        final var videoEncoderCompleted = new VideoEncoderCompleted(expectedId, expectedOutputBucket, expectedMetadata);

        final var actual = this.jacksonTester.write(videoEncoderCompleted);

        assertThat(actual)
            .hasJsonPathValue("$.id", expectedId)
            .hasJsonPathValue("$.output_bucket_path", expectedOutputBucket)
            .hasJsonPathValue("$.status", expectedStatus)
            .hasJsonPathValue("$.video.encoded_video_folder", expectedEncoderVideoFolder)
            .hasJsonPathValue("$.video.resource_id", expectedResourceId)
            .hasJsonPathValue("$.video.file_path", expectedFilePath);
    }

    @Test
    void testUnmarshallErrorResult() throws IOException {
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = IdentifierUtils.unique();
        final var expectedFilePath = "file-path";
        final var expectedVideoMessage = new VideoMessage(expectedResourceId, expectedFilePath);

        final var json = """
            {
                "status": "%s",
                "error": "%s",
                "message": {
                    "resource_id": "%s",
                    "file_path": "%s"
                }
            }
        """.formatted(expectedStatus, expectedMessage, expectedResourceId, expectedFilePath);

        final var actual = this.jacksonTester.parse(json);

        assertThat(actual)
            .isInstanceOf(VideoEncoderError.class)
            .hasFieldOrPropertyWithValue("error", expectedMessage)
            .hasFieldOrPropertyWithValue("message", expectedVideoMessage);
    }

    @Test
    void testMarshallErrorResult() throws IOException {
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = IdentifierUtils.unique();
        final var expectedFilePath = "file-path";
        final var expectedVideoMessage = new VideoMessage(expectedResourceId, expectedFilePath);
        final var videoEncoderError = new VideoEncoderError(expectedVideoMessage, expectedMessage);

        final var actual = this.jacksonTester.write(videoEncoderError);

        assertThat(actual)
            .hasJsonPathValue("$.status", expectedStatus)
            .hasJsonPathValue("$.error", expectedMessage)
            .hasJsonPathValue("$.message.resource_id", expectedResourceId)
            .hasJsonPathValue("$.message.file_path", expectedFilePath);
    }

}
