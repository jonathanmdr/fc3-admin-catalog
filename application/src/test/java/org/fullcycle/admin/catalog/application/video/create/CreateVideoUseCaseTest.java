package org.fullcycle.admin.catalog.application.video.create;

import org.fullcycle.admin.catalog.application.Fixtures;
import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.video.Resource;
import org.fullcycle.admin.catalog.domain.video.VideoGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.fullcycle.admin.catalog.domain.video.Resource.Type;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateVideoUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            categoryGateway,
            genreGateway,
            castMemberGateway,
            videoGateway
        );
    }

    @Test
    void givenAValidCommand_whenCallsCreateVideo_shouldReturnVideoId () {
        final var expectedTitle = Fixtures.VideoFixture.title();
        final var expectedDescription = Fixtures.VideoFixture.description();
        final var expectedLaunchedAt = Year.of(Fixtures.VideoFixture.year());
        final var expectedDuration = Fixtures.VideoFixture.duration();
        final var expectedOpened = Fixtures.VideoFixture.opened();
        final var expectedPublished = Fixtures.VideoFixture.published();
        final var expectedRating = Fixtures.VideoFixture.rating();
        final var expectedCategories = Set.of(Fixtures.CategoryFixture.classes().getId());
        final var expectedGenres = Set.of(Fixtures.GenreFixture.technology().getId());
        final var expectedCastMembers = Set.of(
            Fixtures.CastMemberFixture.wesley().getId(),
            Fixtures.CastMemberFixture.gabriel().getId()
        );
        final Resource expectedVideo = Fixtures.VideoFixture.resource(Type.VIDEO);
        final Resource expectedTrailer = Fixtures.VideoFixture.resource(Type.TRAILER);
        final Resource expectedBanner = Fixtures.VideoFixture.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixtures.VideoFixture.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixtures.VideoFixture.resource(Type.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt.getValue(),
            expectedDuration,
            expectedOpened,
            expectedPublished,
            expectedRating.label(),
            listIdAsString(expectedCategories),
            listIdAsString(expectedGenres),
            listIdAsString(expectedCastMembers),
            expectedVideo,
            expectedTrailer,
            expectedBanner,
            expectedThumbnail,
            expectedThumbnailHalf
        );

        when(categoryGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCategories));
        when(genreGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedGenres));
        when(castMemberGateway.existsByIds(any()))
            .thenReturn(new ArrayList<>(expectedCastMembers));
        when(videoGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(videoGateway).create(argThat(video ->
                Objects.equals(expectedTitle, video.getTitle())
                && Objects.equals(expectedDescription, video.getDescription())
                && Objects.equals(expectedLaunchedAt, video.getLaunchedAt())
                && Objects.equals(expectedDuration, video.getDuration())
                && Objects.equals(expectedOpened, video.isOpened())
                && Objects.equals(expectedPublished, video.isPublished())
                && Objects.equals(expectedRating, video.getRating())
                && Objects.equals(expectedCategories, video.getCategories())
                && Objects.equals(expectedGenres, video.getGenres())
                && Objects.equals(expectedCastMembers, video.getCastMembers())
//                && video.getVideo().isPresent()
//                && video.getTrailer().isPresent()
//                && video.getBanner().isPresent()
//                && video.getThumbnail().isPresent()
//                && video.getThumbnailHalf().isPresent()
//                && Objects.equals(expectedVideo.name(), video.getVideo().get().name())
//                && Objects.equals(expectedTrailer.name(), video.getTrailer().get().name())
//                && Objects.equals(expectedBanner.name(), video.getBanner().get().name())
//                && Objects.equals(expectedThumbnail.name(), video.getThumbnail().get().name())
//                && Objects.equals(expectedThumbnailHalf.name(), video.getThumbnailHalf().get().name())
                && Objects.nonNull(video.getCreatedAt())
                && Objects.nonNull(video.getUpdatedAt())
        ));
    }

}
