package org.fullcycle.admin.catalog.infrastructure.video;

import org.assertj.core.data.Index;
import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoPreview;
import org.fullcycle.admin.catalog.domain.video.VideoSearchQuery;
import org.fullcycle.admin.catalog.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
        final var expectedVideo = AudioVideoMedia.newAudioVideoMedia("video", "a1s2d3", "/media/video");
        final var expectedTrailer = AudioVideoMedia.newAudioVideoMedia("trailer", "d3s2a1", "/media/trailer");
        final var expectedBanner = ImageMedia.newImageMedia("banner", "q1w2e3", "/media/banner");
        final var expectedThumbnail = ImageMedia.newImageMedia("thumbnail", "e3w2q1", "/media/thumbnail");
        final var expectedThumbnailHalf = ImageMedia.newImageMedia("thumbnail_half", "z1x2c3", "/media/thumbnail_half");

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
        video.addAudioVideoMediaVideo(expectedVideo);
        video.addAudioVideoMediaTrailer(expectedTrailer);
        video.addImageMediaBanner(expectedBanner);
        video.addImageMediaThumbnail(expectedThumbnail);
        video.addImageMediaThumbnailHalf(expectedThumbnailHalf);

        final var output = this.videoGateway.create(video);

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

        final var output = this.videoGateway.create(video);

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

    @Test
    @Transactional
    void givenAValidVideo_whenCallsUpdate_shouldPersistIt() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());
        final var video = this.videoGateway.create(
            Video.newVideo(
                Fixtures.VideoFixture.title(),
                Fixtures.VideoFixture.description(),
                Year.of(Fixtures.VideoFixture.year()),
                Fixtures.VideoFixture.duration(),
                Fixtures.VideoFixture.rating(),
                Fixtures.VideoFixture.opened(),
                Fixtures.VideoFixture.published(),
                Set.of(),
                Set.of(),
                Set.of()
            )
        );

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
        final var expectedVideo = AudioVideoMedia.newAudioVideoMedia("video", "a1s2d3", "/media/video");
        final var expectedTrailer = AudioVideoMedia.newAudioVideoMedia("trailer", "d3s2a1", "/media/trailer");
        final var expectedBanner = ImageMedia.newImageMedia("banner", "q1w2e3", "/media/banner");
        final var expectedThumbnail = ImageMedia.newImageMedia("thumbnail", "e3w2q1", "/media/thumbnail");
        final var expectedThumbnailHalf = ImageMedia.newImageMedia("thumbnail_half", "z1x2c3", "/media/thumbnail_half");

        final var updatedVideo = Video.with(video)
            .update(
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
        updatedVideo.addAudioVideoMediaVideo(expectedVideo);
        updatedVideo.addAudioVideoMediaTrailer(expectedTrailer);
        updatedVideo.addImageMediaBanner(expectedBanner);
        updatedVideo.addImageMediaThumbnail(expectedThumbnail);
        updatedVideo.addImageMediaThumbnailHalf(expectedThumbnailHalf);

        final var output = this.videoGateway.update(updatedVideo);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();
        assertThat(output.getId()).isEqualTo(updatedVideo.getId());
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
        assertThat(output.getUpdatedAt()).isAfter(output.getCreatedAt());

        final var persisted = this.videoRepository.findById(output.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected video cannot be null"));

        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getId()).isEqualTo(updatedVideo.getId().getValue());
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
        assertThat(persisted.getUpdatedAt()).isAfter(persisted.getCreatedAt());
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsUpdateWithoutRelations_shouldPersistIt() {
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
        final var expectedVideoToPersist = AudioVideoMedia.newAudioVideoMedia("video", "a1s2d3", "/media/video");
        final var expectedTrailerToPersist = AudioVideoMedia.newAudioVideoMedia("trailer", "d3s2a1", "/media/trailer");
        final var expectedBannerToPersist = ImageMedia.newImageMedia("banner", "q1w2e3", "/media/banner");
        final var expectedThumbnailToPersist = ImageMedia.newImageMedia("thumbnail", "e3w2q1", "/media/thumbnail");
        final var expectedThumbnailHalfToPersist = ImageMedia.newImageMedia("thumbnail_half", "z1x2c3", "/media/thumbnail_half");

        final var createdVideo = Video.newVideo(
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
        createdVideo.addAudioVideoMediaVideo(expectedVideoToPersist);
        createdVideo.addAudioVideoMediaTrailer(expectedTrailerToPersist);
        createdVideo.addImageMediaBanner(expectedBannerToPersist);
        createdVideo.addImageMediaThumbnail(expectedThumbnailToPersist);
        createdVideo.addImageMediaThumbnailHalf(expectedThumbnailHalfToPersist);

        final var video = this.videoGateway.create(createdVideo);

        final var updatedVideo = Video.with(video)
            .update(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                Set.of(),
                Set.of(),
                Set.of()
            );
        updatedVideo.removeAudioVideoMediaVideo();
        updatedVideo.removeAudioVideoMediaTrailer();
        updatedVideo.removeImageMediaBanner();
        updatedVideo.removeImageMediaThumbnail();
        updatedVideo.removeImageMediaThumbnailHalf();

        final var output = this.videoGateway.update(updatedVideo);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();
        assertThat(output.getId()).isEqualTo(updatedVideo.getId());
        assertThat(output.getTitle()).isEqualTo(expectedTitle);
        assertThat(output.getDescription()).isEqualTo(expectedDescription);
        assertThat(output.getLaunchedAt()).isEqualTo(expectedLaunchedAt);
        assertThat(output.getDuration()).isEqualTo(expectedDuration);
        assertThat(output.isOpened()).isEqualTo(expectedOpened);
        assertThat(output.isPublished()).isEqualTo(expectedPublished);
        assertThat(output.getRating()).isEqualTo(expectedRating);
        assertThat(output.getCategories()).isEmpty();
        assertThat(output.getGenres()).isEmpty();
        assertThat(output.getCastMembers()).isEmpty();
        assertThat(output.getVideo()).isEmpty();
        assertThat(output.getTrailer()).isEmpty();
        assertThat(output.getBanner()).isEmpty();
        assertThat(output.getThumbnail()).isEmpty();
        assertThat(output.getThumbnailHalf()).isEmpty();
        assertThat(output.getCreatedAt()).isNotNull();
        assertThat(output.getUpdatedAt()).isNotNull();
        assertThat(output.getUpdatedAt()).isAfter(output.getCreatedAt());

        final var persisted = this.videoRepository.findById(output.getId().getValue())
            .orElseThrow(() -> new IllegalStateException("Expected video cannot be null"));

        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getId()).isEqualTo(updatedVideo.getId().getValue());
        assertThat(persisted.getTitle()).isEqualTo(expectedTitle);
        assertThat(persisted.getDescription()).isEqualTo(expectedDescription);
        assertThat(persisted.getYearLaunched()).isEqualTo(expectedLaunchedAt.getValue());
        assertThat(persisted.getDuration()).isEqualTo(expectedDuration);
        assertThat(persisted.isOpened()).isEqualTo(expectedOpened);
        assertThat(persisted.isPublished()).isEqualTo(expectedPublished);
        assertThat(persisted.getRating()).isEqualTo(expectedRating);
        assertThat(persisted.getCategoryIds()).isEmpty();
        assertThat(persisted.getCategories()).isEmpty();
        assertThat(persisted.getGenreIds()).isEmpty();
        assertThat(persisted.getGenres()).isEmpty();
        assertThat(persisted.getCastMemberIds()).isEmpty();
        assertThat(persisted.getCastMembers()).isEmpty();
        assertThat(persisted.getVideo()).isNull();
        assertThat(persisted.getTrailer()).isNull();
        assertThat(persisted.getBanner()).isNull();
        assertThat(persisted.getThumbnail()).isNull();
        assertThat(persisted.getThumbnailHalf()).isNull();
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isAfter(persisted.getCreatedAt());
    }

    @Test
    void givenAValidVideoId_whenCallsDeleteById_shouldRemoveIt() {
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
        final var expectedVideo = AudioVideoMedia.newAudioVideoMedia("video", "a1s2d3", "/media/video");
        final var expectedTrailer = AudioVideoMedia.newAudioVideoMedia("trailer", "d3s2a1", "/media/trailer");
        final var expectedBanner = ImageMedia.newImageMedia("banner", "q1w2e3", "/media/banner");
        final var expectedThumbnail = ImageMedia.newImageMedia("thumbnail", "e3w2q1", "/media/thumbnail");
        final var expectedThumbnailHalf = ImageMedia.newImageMedia("thumbnail_half", "z1x2c3", "/media/thumbnail_half");

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
        video.addAudioVideoMediaVideo(expectedVideo);
        video.addAudioVideoMediaTrailer(expectedTrailer);
        video.addImageMediaBanner(expectedBanner);
        video.addImageMediaThumbnail(expectedThumbnail);
        video.addImageMediaThumbnailHalf(expectedThumbnailHalf);

        final var output = this.videoGateway.create(video);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();

        assertThat(this.videoRepository.findById(output.getId().getValue())).isPresent();

        assertDoesNotThrow(() -> this.videoGateway.deleteById(output.getId()));

        assertThat(this.videoRepository.findById(output.getId().getValue())).isEmpty();
    }

    @Test
    void givenANotFoundVideoId_whenCallsDeleteById_shouldDoNothing() {
        final var notFoundVideoId = VideoID.unique();

        assertThat(this.videoRepository.findById(notFoundVideoId.getValue())).isEmpty();

        assertDoesNotThrow(() -> this.videoGateway.deleteById(notFoundVideoId));

        assertThat(this.videoRepository.findById(notFoundVideoId.getValue())).isEmpty();
    }

    @Test
    void givenAValidVideoId_whenCallsFindById_shouldReturnVideo() {
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
        final var expectedVideo = AudioVideoMedia.newAudioVideoMedia("video", "a1s2d3", "/media/video");
        final var expectedTrailer = AudioVideoMedia.newAudioVideoMedia("trailer", "d3s2a1", "/media/trailer");
        final var expectedBanner = ImageMedia.newImageMedia("banner", "q1w2e3", "/media/banner");
        final var expectedThumbnail = ImageMedia.newImageMedia("thumbnail", "e3w2q1", "/media/thumbnail");
        final var expectedThumbnailHalf = ImageMedia.newImageMedia("thumbnail_half", "z1x2c3", "/media/thumbnail_half");

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
        video.addAudioVideoMediaVideo(expectedVideo);
        video.addAudioVideoMediaTrailer(expectedTrailer);
        video.addImageMediaBanner(expectedBanner);
        video.addImageMediaThumbnail(expectedThumbnail);
        video.addImageMediaThumbnailHalf(expectedThumbnailHalf);

        final var output = this.videoGateway.create(video);

        final var persisted = this.videoGateway.findById(output.getId())
            .orElseThrow(() -> new IllegalStateException("Expected video cannot be null"));

        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getId()).isEqualTo(video.getId());
        assertThat(persisted.getTitle()).isEqualTo(expectedTitle);
        assertThat(persisted.getDescription()).isEqualTo(expectedDescription);
        assertThat(persisted.getLaunchedAt()).isEqualTo(expectedLaunchedAt);
        assertThat(persisted.getDuration()).isEqualTo(expectedDuration);
        assertThat(persisted.isOpened()).isEqualTo(expectedOpened);
        assertThat(persisted.isPublished()).isEqualTo(expectedPublished);
        assertThat(persisted.getRating()).isEqualTo(expectedRating);
        assertThat(persisted.getVideo()).isPresent();
        assertThat(persisted.getVideo().get()).isEqualTo(expectedVideo);
        assertThat(persisted.getTrailer()).isPresent();
        assertThat(persisted.getTrailer().get()).isEqualTo(expectedTrailer);
        assertThat(persisted.getBanner()).isPresent();
        assertThat(persisted.getBanner().get()).isEqualTo(expectedBanner);
        assertThat(persisted.getThumbnail()).isPresent();
        assertThat(persisted.getThumbnail().get()).isEqualTo(expectedThumbnail);
        assertThat(persisted.getThumbnailHalf()).isPresent();
        assertThat(persisted.getThumbnailHalf().get()).isEqualTo(expectedThumbnailHalf);
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
    }

    @Test
    void givenANotFoundVideoId_whenCallsFindById_shouldReturnEmpty() {
        final var notFoundVideoId = VideoID.unique();
        assertThat(this.videoGateway.findById(notFoundVideoId)).isEmpty();
    }

    @Test
    void givenAValidSearchQueryWithAllParamsToSearch_whenCallsFindAll_shouldReturnVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design Hack",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD Hack",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture Hack",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform Hack",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var systemDesignVideoPreview = VideoPreview.from(systemDesign);
        final var dddVideoPreview = VideoPreview.from(ddd);
        final var cleanArchitectureVideoPreview = VideoPreview.from(cleanArchitecture);
        final var kubernetesVideoPreview = VideoPreview.from(kubernetes);

        final var expectedVideos = List.of(
            systemDesignVideoPreview,
            dddVideoPreview,
            cleanArchitectureVideoPreview,
            kubernetesVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "hack";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 4;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(systemDesignVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(dddVideoPreview, Index.atIndex(1));
        assertThat(output.items()).contains(cleanArchitectureVideoPreview, Index.atIndex(2));
        assertThat(output.items()).contains(kubernetesVideoPreview, Index.atIndex(3));
    }

    @Test
    void givenAValidSearchQueryWithoutTerms_whenCallsFindAll_shouldReturnVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var systemDesignVideoPreview = VideoPreview.from(systemDesign);
        final var dddVideoPreview = VideoPreview.from(ddd);
        final var cleanArchitectureVideoPreview = VideoPreview.from(cleanArchitecture);
        final var kubernetesVideoPreview = VideoPreview.from(kubernetes);

        final var expectedVideos = List.of(
            systemDesignVideoPreview,
            dddVideoPreview,
            cleanArchitectureVideoPreview,
            kubernetesVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 4;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(systemDesignVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(dddVideoPreview, Index.atIndex(1));
        assertThat(output.items()).contains(cleanArchitectureVideoPreview, Index.atIndex(2));
        assertThat(output.items()).contains(kubernetesVideoPreview, Index.atIndex(3));
    }

    @Test
    void givenAValidSearchQueryWithTerms_whenCallsFindAll_shouldReturnFilteredVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var kubernetesVideoPreview = VideoPreview.from(kubernetes);

        final var expectedVideos = List.of(
            kubernetesVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "k8s";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 1;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(kubernetesVideoPreview, Index.atIndex(0));
    }

    @Test
    void givenAValidSearchQueryWithCategory_whenCallsFindAll_shouldReturnFilteredVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var dddVideoPreview = VideoPreview.from(ddd);
        final var kubernetesVideoPreview = VideoPreview.from(kubernetes);

        final var expectedVideos = List.of(
            dddVideoPreview,
            kubernetesVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 2;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(category.getId()),
            Set.of(),
            Set.of()
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(dddVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(kubernetesVideoPreview, Index.atIndex(1));
    }

    @Test
    void givenAValidSearchQueryWithGenre_whenCallsFindAll_shouldReturnFilteredVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var systemDesignVideoPreview = VideoPreview.from(systemDesign);
        final var cleanArchitectureVideoPreview = VideoPreview.from(cleanArchitecture);

        final var expectedVideos = List.of(
            systemDesignVideoPreview,
            cleanArchitectureVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 2;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(genre.getId()),
            Set.of()
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(systemDesignVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(cleanArchitectureVideoPreview, Index.atIndex(1));
    }

    @Test
    void givenAValidSearchQueryWithCastMember_whenCallsFindAll_shouldReturnFilteredVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of()
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of()
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var systemDesignVideoPreview = VideoPreview.from(systemDesign);
        final var dddVideoPreview = VideoPreview.from(ddd);

        final var expectedVideos = List.of(
            systemDesignVideoPreview,
            dddVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 2;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of(castMember.getId())
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(systemDesignVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(dddVideoPreview, Index.atIndex(1));
    }

    @Test
    void givenAValidSearchQueryWithSortByTitle_whenCallsFindAll_shouldReturnOrderedVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var systemDesignVideoPreview = VideoPreview.from(systemDesign);
        final var dddVideoPreview = VideoPreview.from(ddd);
        final var cleanArchitectureVideoPreview = VideoPreview.from(cleanArchitecture);
        final var kubernetesVideoPreview = VideoPreview.from(kubernetes);

        final var expectedVideos = List.of(
            cleanArchitectureVideoPreview,
            dddVideoPreview,
            kubernetesVideoPreview,
            systemDesignVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 4;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(cleanArchitectureVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(dddVideoPreview, Index.atIndex(1));
        assertThat(output.items()).contains(kubernetesVideoPreview, Index.atIndex(2));
        assertThat(output.items()).contains(systemDesignVideoPreview, Index.atIndex(3));
    }

    @Test
    void givenAValidSearchQueryWithDirectionDesc_whenCallsFindAll_shouldReturnOrderedVideoList() {
        final var category = this.categoryGateway.create(Fixtures.CategoryFixture.classes());
        final var genre = this.genreGateway.create(Fixtures.GenreFixture.technology());
        final var castMember = this.castMemberGateway.create(Fixtures.CastMemberFixture.wesley());

        final var systemDesign = Video.newVideo(
            "System Design",
            "System Design Interview",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var ddd = Video.newVideo(
            "DDD",
            "Domain Driven Design",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var cleanArchitecture = Video.newVideo(
            "Clean Architecture",
            "Clean Architecture Concepts",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        final var kubernetes = Video.newVideo(
            "K8s the Kubernetes Platform",
            "Kubernetes Platform",
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(category.getId()),
            Set.of(genre.getId()),
            Set.of(castMember.getId())
        );

        this.videoGateway.create(systemDesign);
        this.videoGateway.create(ddd);
        this.videoGateway.create(cleanArchitecture);
        this.videoGateway.create(kubernetes);

        final var systemDesignVideoPreview = VideoPreview.from(systemDesign);
        final var dddVideoPreview = VideoPreview.from(ddd);
        final var cleanArchitectureVideoPreview = VideoPreview.from(cleanArchitecture);
        final var kubernetesVideoPreview = VideoPreview.from(kubernetes);

        final var expectedVideos = List.of(
            kubernetesVideoPreview,
            cleanArchitectureVideoPreview,
            dddVideoPreview,
            systemDesignVideoPreview
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 4;

        final var searchQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedItemsCount,
            expectedVideos
        );

        final var output = this.videoGateway.findAll(searchQuery);

        assertThat(output).isNotNull();
        assertThat(output.currentPage()).isEqualTo(expectedPage);
        assertThat(output.perPage()).isEqualTo(expectedPerPage);
        assertThat(output.total()).isEqualTo(expectedItemsCount);
        assertThat(output).isEqualTo(expectedPagination);
        assertThat(output.items()).contains(kubernetesVideoPreview, Index.atIndex(0));
        assertThat(output.items()).contains(cleanArchitectureVideoPreview, Index.atIndex(1));
        assertThat(output.items()).contains(dddVideoPreview, Index.atIndex(2));
        assertThat(output.items()).contains(systemDesignVideoPreview, Index.atIndex(3));
    }

}
