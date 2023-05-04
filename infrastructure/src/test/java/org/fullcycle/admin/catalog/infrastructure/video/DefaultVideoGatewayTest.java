package org.fullcycle.admin.catalog.infrastructure.video;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class DefaultVideoGatewayTest {

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Test
    void testInjection() {
        assertThat(castMemberGateway).isNotNull();
        assertThat(genreGateway).isNotNull();
        assertThat(castMemberGateway).isNotNull();
        assertThat(videoRepository).isNotNull();
        assertThat(videoGateway).isNotNull();
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsCreate_shouldPersistIt() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(category.getId());
        final var expectedGenres = Set.of(genre.getId());
        final var expectedCastMembers = Set.of(castMember.getId());

        final var video = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedCastMembers
        );

        final var expectedVideo = AudioVideoMedia.newAudioVideoMedia("video", "a1s2d3", "/media/video");
        final var expectedTrailer = AudioVideoMedia.newAudioVideoMedia("trailer", "d3s2a1", "/media/trailer");
        final var expectedBanner = ImageMedia.newImageMedia("banner", "q1w2e3", "/media/banner");
        final var expectedThumbnail = ImageMedia.newImageMedia("thumbnail", "e3w2q1", "/media/thumbnail");
        final var expectedThumbnailHalf = ImageMedia.newImageMedia("thumbnail_half", "z1x2c3", "/media/thumbnail_half");

        video.addAudioVideoMediaVideo(expectedVideo);
        video.addAudioVideoMediaTrailer(expectedTrailer);
        video.addImageMediaBanner(expectedBanner);
        video.addImageMediaThumbnail(expectedThumbnail);
        video.addImageMediaThumbnailHalf(expectedThumbnailHalf);

        final var output = videoGateway.create(video);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();
        assertThat(output.getId()).isEqualTo(video.getId());
        assertThat(output.getTitle()).isEqualTo(expectedTitle);
        assertThat(output.getDescription()).isEqualTo(expectedDescription);
        assertThat(output.getLaunchedAt()).isEqualTo(expectedLaunchedAt);
        assertThat(output.getDuration()).isEqualTo(expectedDuration);
        assertThat(output.isOpened()).isEqualTo(expectedOpened);
        assertThat(output.isPublished()).isEqualTo(expectedPublished);
        assertThat(output.getRating()).isEqualTo(expectedRating);
        assertThat(output.getCategories()).isEqualTo(expectedCategories);
        assertThat(output.getGenres()).isEqualTo(expectedGenres);
        assertThat(output.getCastMembers()).isEqualTo(expectedCastMembers);
        assertThat(output.getVideo()).isPresent();
        assertThat(output.getTrailer()).isPresent();
        assertThat(output.getBanner()).isPresent();
        assertThat(output.getThumbnail()).isPresent();
        assertThat(output.getThumbnailHalf()).isPresent();
        assertThat(output.getVideo().get()).isEqualTo(expectedVideo);
        assertThat(output.getTrailer().get()).isEqualTo(expectedTrailer);
        assertThat(output.getBanner().get()).isEqualTo(expectedBanner);
        assertThat(output.getThumbnail().get()).isEqualTo(expectedThumbnail);
        assertThat(output.getThumbnailHalf().get()).isEqualTo(expectedThumbnailHalf);
        assertThat(output.getCreatedAt()).isNotNull();
        assertThat(output.getUpdatedAt()).isNotNull();

        final var persisted = this.videoRepository.findById(output.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected video cannot be null"));

        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getId()).isEqualTo(video.getId().getValue());
        assertThat(persisted.getTitle()).isEqualTo(expectedTitle);
        assertThat(persisted.getDescription()).isEqualTo(expectedDescription);
        assertThat(persisted.getYearLaunched()).isEqualTo(expectedLaunchedAt.getValue());
        assertThat(persisted.getDuration()).isEqualTo(expectedDuration);
        assertThat(persisted.isOpened()).isEqualTo(expectedOpened);
        assertThat(persisted.isPublished()).isEqualTo(expectedPublished);
        assertThat(persisted.getRating()).isEqualTo(expectedRating);
        assertThat(persisted.getCategoryIds()).isEqualTo(expectedCategories);
        assertThat(persisted.getGenreIds()).isEqualTo(expectedGenres);
        assertThat(persisted.getCastMemberIds()).isEqualTo(expectedCastMembers);
        assertThat(persisted.getVideo().toDomain()).isEqualTo(expectedVideo);
        assertThat(persisted.getTrailer().toDomain()).isEqualTo(expectedTrailer);
        assertThat(persisted.getBanner().toDomain()).isEqualTo(expectedBanner);
        assertThat(persisted.getThumbnail().toDomain()).isEqualTo(expectedThumbnail);
        assertThat(persisted.getThumbnailHalf().toDomain()).isEqualTo(expectedThumbnailHalf);
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
    }

    @Test
    @Transactional
    void givenAValidVideoWithoutRelation_whenCallsCreate_shouldPersistIt() {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final var video = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedCastMembers
        );

        final var output = videoGateway.create(video);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();
        assertThat(output.getId()).isEqualTo(video.getId());
        assertThat(output.getTitle()).isEqualTo(expectedTitle);
        assertThat(output.getDescription()).isEqualTo(expectedDescription);
        assertThat(output.getLaunchedAt()).isEqualTo(expectedLaunchedAt);
        assertThat(output.getDuration()).isEqualTo(expectedDuration);
        assertThat(output.isOpened()).isEqualTo(expectedOpened);
        assertThat(output.isPublished()).isEqualTo(expectedPublished);
        assertThat(output.getRating()).isEqualTo(expectedRating);
        assertThat(output.getCategories()).isEqualTo(expectedCategories);
        assertThat(output.getGenres()).isEqualTo(expectedGenres);
        assertThat(output.getCastMembers()).isEqualTo(expectedCastMembers);
        assertThat(output.getVideo()).isEmpty();
        assertThat(output.getTrailer()).isEmpty();
        assertThat(output.getBanner()).isEmpty();
        assertThat(output.getThumbnail()).isEmpty();
        assertThat(output.getThumbnailHalf()).isEmpty();
        assertThat(output.getCreatedAt()).isNotNull();
        assertThat(output.getUpdatedAt()).isNotNull();

        final var persisted = this.videoRepository.findById(output.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected video cannot be null"));

        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getId()).isEqualTo(video.getId().getValue());
        assertThat(persisted.getTitle()).isEqualTo(expectedTitle);
        assertThat(persisted.getDescription()).isEqualTo(expectedDescription);
        assertThat(persisted.getYearLaunched()).isEqualTo(expectedLaunchedAt.getValue());
        assertThat(persisted.getDuration()).isEqualTo(expectedDuration);
        assertThat(persisted.isOpened()).isEqualTo(expectedOpened);
        assertThat(persisted.isPublished()).isEqualTo(expectedPublished);
        assertThat(persisted.getRating()).isEqualTo(expectedRating);
        assertThat(persisted.getCategoryIds()).isEqualTo(expectedCategories);
        assertThat(persisted.getGenreIds()).isEqualTo(expectedGenres);
        assertThat(persisted.getCastMemberIds()).isEqualTo(expectedCastMembers);
        assertThat(persisted.getVideo()).isNull();
        assertThat(persisted.getTrailer()).isNull();
        assertThat(persisted.getBanner()).isNull();
        assertThat(persisted.getThumbnail()).isNull();
        assertThat(persisted.getThumbnailHalf()).isNull();
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
    }

}
