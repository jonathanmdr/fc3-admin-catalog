package org.fullcycle.admin.catalog.application;

import com.github.javafaker.Faker;
import org.fullcycle.admin.catalog.domain.castmember.CastMember;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.video.Rating;
import org.fullcycle.admin.catalog.domain.video.Resource;
import org.fullcycle.admin.catalog.domain.video.Video;

import java.time.Year;
import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.List;
import static io.vavr.API.Match;
import static org.fullcycle.admin.catalog.domain.video.Resource.Type;
import static org.fullcycle.admin.catalog.domain.video.Resource.with;

public final class Fixtures {

    private static final Faker FAKER = new Faker();

    private Fixtures() { }

    public static final class CategoryFixture {

        private static final Category CLASSES = Category.newCategory("Classes", "Technology classes", true);

        public static Category classes() {
            return Category.with(CLASSES);
        }

    }

    public static final class GenreFixture {

        private static final Genre TECHNOLOGY = Genre.newGenre("Technology", true);

        public static Genre technology() {
            return Genre.with(TECHNOLOGY);
        }

    }

    public static final class CastMemberFixture {

        private static final CastMember WESLEY_FC = CastMember.newMember("Wesley FullCycle", CastMemberType.ACTOR);
        private static final CastMember GABRIEL_FC = CastMember.newMember("Gabriel FullCycle", CastMemberType.ACTOR);

        public static CastMember wesley() {
            return CastMember.with(WESLEY_FC);
        }

        public static CastMember gabriel() {
            return CastMember.with(GABRIEL_FC);
        }

    }

    public static final class VideoFixture {

        private static final Video ANY_VIDEO = Video.newVideo(
                title(),
                description(),
                Year.of(year()),
                duration(),
                rating(),
                opened(),
                published(),
                Set.of(CategoryFixture.classes().getId()),
                Set.of(GenreFixture.technology().getId()),
                Set.of(CastMemberFixture.wesley().getId(), CastMemberFixture.gabriel().getId())
        );

        public static Video video() {
            return Video.with(ANY_VIDEO);
        }

        public static String title() {
            return FAKER.options()
                .option(
                    "System Design Interview",
                    "DDD And Clean Architecture",
                    "Kubernetes Orchestrator"
                );
        }

        public static String description() {
            return FAKER.options()
                .option(
                    "This is a simple interview",
                    "This is the best practices for software development",
                    "How to orchestrate your distributed applications"
                );
        }

        public static Integer year() {
            return FAKER.random()
                .nextInt(2020, 2030);
        }

        public static Double duration() {
            return FAKER.options()
                .option(
                    120.0,
                    15.5,
                    35.5,
                    10.0,
                    2.0
                );
        }

        public static Rating rating() {
            return FAKER.options()
                .option(Rating.values());
        }

        public static Boolean opened() {
            return FAKER.bool().bool();
        }

        public static Boolean published() {
            return FAKER.bool().bool();
        }

        public static Resource resource(final Type resourceType) {
            final String contentType = Match(resourceType).of(
                Case($(List(Type.VIDEO, Type.TRAILER)::contains), "video/mp4"),
                Case($(), "image/jpeg")
            );
            final var content = "AnyContent".getBytes();
            return with(
                content,
                contentType,
                resourceType.name().toLowerCase(),
                resourceType
            );
        }

    }
}
