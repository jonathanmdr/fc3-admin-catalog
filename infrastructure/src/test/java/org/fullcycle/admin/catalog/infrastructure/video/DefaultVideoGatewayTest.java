package org.fullcycle.admin.catalog.infrastructure.video;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.video.AudioVideoMedia;
import org.fullcycle.admin.catalog.domain.video.ImageMedia;
import org.fullcycle.admin.catalog.domain.video.Video;
import org.fullcycle.admin.catalog.domain.video.VideoID;
import org.fullcycle.admin.catalog.domain.video.VideoSearchQuery;
import org.fullcycle.admin.catalog.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private VideoRepository videoRepository;

    private CastMember wesley;
    private CastMember gabriel;

    private Category classes;
    private Category lives;

    private Genre technology;
    private Genre business;

    @BeforeEach
    public void setUp() {
        wesley = castMemberGateway.create(Fixtures.CastMemberFixture.wesley());
        gabriel = castMemberGateway.create(Fixtures.CastMemberFixture.gabriel());

        classes = categoryGateway.create(Fixtures.CategoryFixture.classes());
        lives = categoryGateway.create(Fixtures.CategoryFixture.lives());

        technology = genreGateway.create(Fixtures.GenreFixture.technology());
        business = genreGateway.create(Fixtures.GenreFixture.business());
    }

    @Test
    void testInjection() {
        assertNotNull(videoGateway);
        assertNotNull(castMemberGateway);
        assertNotNull(categoryGateway);
        assertNotNull(genreGateway);
        assertNotNull(videoRepository);
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsCreate_shouldPersistIt() {
        // given
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchYear = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(classes.getId());
        final var expectedGenres = Set.of(technology.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.newAudioVideoMedia("123", "video", "/media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.newAudioVideoMedia("123", "trailer", "/media/trailer");
        final ImageMedia expectedBanner = ImageMedia.newImageMedia("123", "banner", "/media/banner");
        final ImageMedia expectedThumb = ImageMedia.newImageMedia("123", "thumb", "/media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.newImageMedia("123", "thumbHalf", "/media/thumbHalf");

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchYear,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedMembers
        )
        .updateVideoMedia(expectedVideo)
        .updateTrailerMedia(expectedTrailer)
        .updateBannerMedia(expectedBanner)
        .updateThumbnailMedia(expectedThumb)
        .updateThumbnailHalfMedia(expectedThumbHalf);

        // when
        final var actualVideo = videoGateway.create(aVideo);

        // then
        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedCategories, persistedVideo.getCategoryIds());
        Assertions.assertEquals(expectedGenres, persistedVideo.getGenreIds());
        Assertions.assertEquals(expectedMembers, persistedVideo.getCastMemberIds());
        Assertions.assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        Assertions.assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        Assertions.assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        Assertions.assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        Assertions.assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
    }

    @Test
    @Transactional
    void givenAValidVideoWithoutRelations_whenCallsCreate_shouldPersistIt() {
        // given
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchYear = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Collections.<CategoryID>emptySet();
        final var expectedGenres = Collections.<GenreID>emptySet();
        final var expectedMembers = Collections.<CastMemberID>emptySet();

        final var aVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchYear,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenres,
            expectedMembers
        );

        // when
        final var actualVideo = videoGateway.create(aVideo);

        // then
        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertTrue(actualVideo.getVideo().isEmpty());
        Assertions.assertTrue(actualVideo.getTrailer().isEmpty());
        Assertions.assertTrue(actualVideo.getBanner().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnail().isEmpty());
        Assertions.assertTrue(actualVideo.getThumbnailHalf().isEmpty());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedCategories, persistedVideo.getCategoryIds());
        Assertions.assertEquals(expectedGenres, persistedVideo.getGenreIds());
        Assertions.assertEquals(expectedMembers, persistedVideo.getCastMemberIds());
        Assertions.assertNull(persistedVideo.getVideo());
        Assertions.assertNull(persistedVideo.getTrailer());
        Assertions.assertNull(persistedVideo.getBanner());
        Assertions.assertNull(persistedVideo.getThumbnail());
        Assertions.assertNull(persistedVideo.getThumbnailHalf());
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsUpdate_shouldPersistIt() {
        // given
        final var aVideo = videoGateway.create(Video.newVideo(
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
        ));

        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchYear = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(classes.getId());
        final var expectedGenres = Set.of(technology.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.newAudioVideoMedia("123", "video", "/media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.newAudioVideoMedia("123", "trailer", "/media/trailer");
        final ImageMedia expectedBanner = ImageMedia.newImageMedia("123", "banner", "/media/banner");
        final ImageMedia expectedThumb = ImageMedia.newImageMedia("123", "thumb", "/media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.newImageMedia("123", "thumbHalf", "/media/thumbHalf");

        final var updatedVideo = Video.with(aVideo)
            .update(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedMembers
            )
            .updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb)
            .updateThumbnailHalfMedia(expectedThumbHalf);

        // when
        final var actualVideo = videoGateway.update(updatedVideo);

        // then
        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());
        assertNotNull(actualVideo.getCreatedAt());
        Assertions.assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        Assertions.assertEquals(expectedTitle, persistedVideo.getTitle());
        Assertions.assertEquals(expectedDescription, persistedVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, Year.of(persistedVideo.getYearLaunched()));
        Assertions.assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertEquals(expectedOpened, persistedVideo.isOpened());
        Assertions.assertEquals(expectedPublished, persistedVideo.isPublished());
        Assertions.assertEquals(expectedRating, persistedVideo.getRating());
        Assertions.assertEquals(expectedCategories, persistedVideo.getCategoryIds());
        Assertions.assertEquals(expectedGenres, persistedVideo.getGenreIds());
        Assertions.assertEquals(expectedMembers, persistedVideo.getCastMemberIds());
        Assertions.assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
        Assertions.assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        Assertions.assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        Assertions.assertEquals(expectedThumb.name(), persistedVideo.getThumbnail().getName());
        Assertions.assertEquals(expectedThumbHalf.name(), persistedVideo.getThumbnailHalf().getName());
        assertNotNull(persistedVideo.getCreatedAt());
        Assertions.assertTrue(persistedVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
    }

    @Test
    void givenAValidVideoId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        final var aVideo = videoGateway.create(Video.newVideo(
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
        ));

        Assertions.assertEquals(1, videoRepository.count());

        final var anId = aVideo.getId();

        // when
        videoGateway.deleteById(anId);

        // then
        Assertions.assertEquals(0, videoRepository.count());
    }

    @Test
    void givenAnInvalidVideoId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        videoGateway.create(Video.newVideo(
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
        ));

        Assertions.assertEquals(1, videoRepository.count());

        final var anId = VideoID.unique();

        // when
        videoGateway.deleteById(anId);

        // then
        Assertions.assertEquals(1, videoRepository.count());
    }

    @Test
    void givenAValidVideo_whenCallsFindById_shouldReturnIt() {
        // given
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchYear = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(classes.getId());
        final var expectedGenres = Set.of(technology.getId());
        final var expectedMembers = Set.of(wesley.getId());

        final AudioVideoMedia expectedVideo = AudioVideoMedia.newAudioVideoMedia("123", "video", "/media/video");
        final AudioVideoMedia expectedTrailer = AudioVideoMedia.newAudioVideoMedia("123", "trailer", "/media/trailer");
        final ImageMedia expectedBanner = ImageMedia.newImageMedia("123", "banner", "/media/banner");
        final ImageMedia expectedThumb = ImageMedia.newImageMedia("123", "thumb", "/media/thumb");
        final ImageMedia expectedThumbHalf = ImageMedia.newImageMedia("123", "thumbHalf", "/media/thumbHalf");

        final var aVideo = videoGateway.create(
            Video.newVideo(
                    expectedTitle,
                    expectedDescription,
                    expectedLaunchYear,
                    expectedDuration,
                    expectedRating,
                    expectedOpened,
                    expectedPublished,
                    expectedCategories,
                    expectedGenres,
                    expectedMembers
            )
            .updateVideoMedia(expectedVideo)
            .updateTrailerMedia(expectedTrailer)
            .updateBannerMedia(expectedBanner)
            .updateThumbnailMedia(expectedThumb)
            .updateThumbnailHalfMedia(expectedThumbHalf)
        );

        // when
        final var actualVideo = videoGateway.findById(aVideo.getId()).get();

        // then
        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        Assertions.assertEquals(expectedTitle, actualVideo.getTitle());
        Assertions.assertEquals(expectedDescription, actualVideo.getDescription());
        Assertions.assertEquals(expectedLaunchYear, actualVideo.getLaunchedAt());
        Assertions.assertEquals(expectedDuration, actualVideo.getDuration());
        Assertions.assertEquals(expectedOpened, actualVideo.isOpened());
        Assertions.assertEquals(expectedPublished, actualVideo.isPublished());
        Assertions.assertEquals(expectedRating, actualVideo.getRating());
        Assertions.assertEquals(expectedCategories, actualVideo.getCategories());
        Assertions.assertEquals(expectedGenres, actualVideo.getGenres());
        Assertions.assertEquals(expectedMembers, actualVideo.getCastMembers());
        Assertions.assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.name(), actualVideo.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.name(), actualVideo.getThumbnailHalf().get().name());
    }

    @Test
    void givenAInvalidVideoId_whenCallsFindById_shouldEmpty() {
        // given
        videoGateway.create(Video.newVideo(
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
        ));

        final var anId = VideoID.unique();

        // when
        final var actualVideo = videoGateway.findById(anId);

        // then
        Assertions.assertTrue(actualVideo.isEmpty());
    }

    @Test
    void givenEmptyParams_whenCallFindAll_shouldReturnAllList() {
        // given
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 4;

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    void givenEmptyVideos_whenCallFindAll_shouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    void givenAValidCategory_whenCallFindAll_shouldReturnFilteredList() {
        // given
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(classes.getId()),
            Set.of(),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());

        Assertions.assertEquals("21.1 Implementação dos testes integrados do findAll", actualPage.items().get(0).title());
        Assertions.assertEquals("Aula de empreendedorismo", actualPage.items().get(1).title());
    }

    @Test
    void givenAValidCastMember_whenCallFindAll_shouldReturnFilteredList() {
        // given
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of(wesley.getId())
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());

        Assertions.assertEquals("Aula de empreendedorismo", actualPage.items().get(0).title());
        Assertions.assertEquals("System Design no Mercado Livre na prática", actualPage.items().get(1).title());
    }

    @Test
    void givenAValidGenre_whenCallFindAll_shouldReturnFilteredList() {
        // given
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(business.getId()),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());

        Assertions.assertEquals("Aula de empreendedorismo", actualPage.items().get(0).title());
    }

    @Test
    void givenAllParameters_whenCallFindAll_shouldReturnFilteredList() {
        // given
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "empreendedorismo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(classes.getId()),
            Set.of(business.getId()),
            Set.of(wesley.getId())
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());

        Assertions.assertEquals("Aula de empreendedorismo", actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
        "0,2,2,4,21.1 Implementação dos testes integrados do findAll;Aula de empreendedorismo",
        "1,2,2,4,Não cometa esses erros ao trabalhar com Microsserviços;System Design no Mercado Livre na prática",
    })
    void givenAValidPaging_whenCallsFindAll_shouldReturnPaged(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedVideos
    ) {
        // given
        mockVideos();

        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final var expectedTitle : expectedVideos.split(";")) {
            final var actualTitle = actualPage.items().get(index).title();
            Assertions.assertEquals(expectedTitle, actualTitle);
            index++;
        }
    }

    @ParameterizedTest
    @CsvSource({
        "system,0,10,1,1,System Design no Mercado Livre na prática",
        "microsser,0,10,1,1,Não cometa esses erros ao trabalhar com Microsserviços",
        "empreendedorismo,0,10,1,1,Aula de empreendedorismo",
        "21,0,10,1,1,21.1 Implementação dos testes integrados do findAll",
    })
    void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedVideo
    ) {
        // given
        mockVideos();

        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedVideo, actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
        "title,asc,0,10,4,4,21.1 Implementação dos testes integrados do findAll",
        "title,desc,0,10,4,4,System Design no Mercado Livre na prática",
        "createdAt,asc,0,10,4,4,System Design no Mercado Livre na prática",
        "createdAt,desc,0,10,4,4,Aula de empreendedorismo",
    })
    void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnOrdered(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedVideo
    ) {
        // given
        mockVideos();

        final var expectedTerms = "";

        final var aQuery = new VideoSearchQuery(
            expectedPage,
            expectedPerPage,
            expectedTerms,
            expectedSort,
            expectedDirection,
            Set.of(),
            Set.of(),
            Set.of()
        );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedVideo, actualPage.items().get(0).title());
    }

    private void mockVideos() {
        videoGateway.create(Video.newVideo(
            "System Design no Mercado Livre na prática",
            Fixtures.VideoFixture.description(),
            Year.of(2022),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(lives.getId()),
            Set.of(technology.getId()),
            Set.of(wesley.getId(), gabriel.getId())
        ));

        videoGateway.create(Video.newVideo(
            "Não cometa esses erros ao trabalhar com Microsserviços",
            Fixtures.VideoFixture.description(),
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(),
            Set.of(),
            Set.of()
        ));

        videoGateway.create(Video.newVideo(
            "21.1 Implementação dos testes integrados do findAll",
            Fixtures.VideoFixture.description(),
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(classes.getId()),
            Set.of(technology.getId()),
            Set.of(gabriel.getId())
        ));

        videoGateway.create(Video.newVideo(
            "Aula de empreendedorismo",
            Fixtures.VideoFixture.description(),
            Year.of(Fixtures.VideoFixture.year()),
            Fixtures.VideoFixture.duration(),
            Fixtures.VideoFixture.rating(),
            Fixtures.VideoFixture.opened(),
            Fixtures.VideoFixture.published(),
            Set.of(classes.getId()),
            Set.of(business.getId()),
            Set.of(wesley.getId())
        ));
    }

}
