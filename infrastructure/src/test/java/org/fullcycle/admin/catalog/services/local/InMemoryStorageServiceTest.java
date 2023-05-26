package org.fullcycle.admin.catalog.services.local;

import org.fullcycle.admin.catalog.domain.Fixtures;
import org.fullcycle.admin.catalog.domain.video.MediaType;
import org.fullcycle.admin.catalog.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryStorageServiceTest {

    private final InMemoryStorageService service = new InMemoryStorageService();

    @BeforeEach
    void setup() {
        this.service.reset();
    }

    @Test
    void givenAValidParams_whenCallsStore_shouldBeSaveIt() {
        final var resource = Fixtures.ResourceFixture.resource(MediaType.TRAILER);

        assertThat(this.service.storage().size()).isZero();

        this.service.store(resource.name(), resource);

        final var actual = this.service.storage().get(resource.name());

        assertThat(this.service.storage().size()).isOne();
        assertThat(actual).isEqualTo(resource);
    }

    @Test
    void givenAnInvalidParams_whenCallsStore_shouldBeThrownException() {
        final var resource = Fixtures.ResourceFixture.resource(MediaType.TRAILER);

        assertThatThrownBy(() -> this.service.store(null, resource))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fileName cannot be null");

        assertThatThrownBy(() -> this.service.store(resource.name(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("resource cannot be null");
    }

    @Test
    void givenAValidParam_whenCallsGet_shouldBeReturnResource() {
        final var resource = Fixtures.ResourceFixture.resource(MediaType.TRAILER);

        this.service.store(resource.name(), resource);

        final var actual = this.service.get(resource.name());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(resource);
    }

    @Test
    void givenAnInvalidParam_whenCallsGet_shouldBeReturnEmpty() {
        final var resource = Fixtures.ResourceFixture.resource(MediaType.TRAILER);

        this.service.store(resource.name(), resource);

        var actual = this.service.get("invalid_name");
        assertThat(this.service.storage().size()).isOne();

        assertThat(actual).isEmpty();

        actual = this.service.get("  ");

        assertThat(actual).isEmpty();

        actual = this.service.get(null);

        assertThat(actual).isEmpty();
    }

    @Test
    void givenAValidParam_whenCallsFindAll_shouldBeReturnResourceNameList() {
        final var trailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var banner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var thumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);

        this.service.store(trailer.name(), trailer);
        this.service.store(banner.name(), banner);
        this.service.store(thumbnail.name(), thumbnail);

        assertThat(this.service.storage().size()).isEqualTo(3);

        final var expected = List.of(MediaType.TRAILER.name().toLowerCase());
        final var actual = this.service.findAll("tra");

        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isOne();
        assertThat(actual).isEqualTo(expected);
        assertThat(this.service.get(trailer.name())).isEqualTo(Optional.of(trailer));
    }

    @Test
    void givenAValidParam_whenCallsFindAllWithNotFoundPrefix_shouldBeReturnEmptyList() {
        final var trailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var banner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var thumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);

        this.service.store(trailer.name(), trailer);
        this.service.store(banner.name(), banner);
        this.service.store(thumbnail.name(), thumbnail);

        final var expected = List.of();
        final var actual = this.service.findAll("foo");

        assertThat(actual).isEmpty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenAnInvalidParam_whenCallsFindAll_shouldBeReturnEmptyList() {
        final var trailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var banner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var thumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);

        this.service.store(trailer.name(), trailer);
        this.service.store(banner.name(), banner);
        this.service.store(thumbnail.name(), thumbnail);

        final var expected = List.of();
        var actual = this.service.findAll(null);

        assertThat(actual).isEmpty();
        assertThat(actual).isEqualTo(expected);

        actual = this.service.findAll("  ");

        assertThat(actual).isEmpty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenAValidParam_whenCallsDeleteAll_shouldBeDeleteAllResources() {
        final var trailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var banner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var thumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);

        this.service.store(trailer.name(), trailer);
        this.service.store(banner.name(), banner);
        this.service.store(thumbnail.name(), thumbnail);

        assertThat(this.service.storage().size()).isEqualTo(3);

        this.service.deleteAll(List.of(trailer.name(), banner.name()));

        final var actual = this.service.storage();

        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isOne();
        assertThat(actual.get(thumbnail.name())).isEqualTo(thumbnail);
    }

    @Test
    void givenAnInvalidParam_whenCallsDeleteAll_shouldBeDoNothing() {
        final var trailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var banner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var thumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);

        this.service.store(trailer.name(), trailer);
        this.service.store(banner.name(), banner);
        this.service.store(thumbnail.name(), thumbnail);

        assertThat(this.service.storage().size()).isEqualTo(3);

        this.service.deleteAll(List.of("bla", "foo"));

        final var actual = this.service.storage();

        final var expected = Map.of(
            trailer.name(), trailer,
            banner.name(), banner,
            thumbnail.name(), thumbnail
        );

        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenAnEmptyListParam_whenCallsDeleteAll_shouldBeDoNothing() {
        final var trailer = Fixtures.ResourceFixture.resource(MediaType.TRAILER);
        final var banner = Fixtures.ResourceFixture.resource(MediaType.BANNER);
        final var thumbnail = Fixtures.ResourceFixture.resource(MediaType.THUMBNAIL);

        this.service.store(trailer.name(), trailer);
        this.service.store(banner.name(), banner);
        this.service.store(thumbnail.name(), thumbnail);

        assertThat(this.service.storage().size()).isEqualTo(3);

        this.service.deleteAll(List.of());

        final var actual = this.service.storage();

        final var expected = Map.of(
            trailer.name(), trailer,
            banner.name(), banner,
            thumbnail.name(), thumbnail
        );

        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual).isEqualTo(expected);
    }

}
