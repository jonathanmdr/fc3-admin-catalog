package org.fullcycle.admin.catalog.application.genre.create;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@IntegrationTest
class CreateGenreUseCaseIT {

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private CreateGenreUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedCategory = Category.newCategory("Movies", null, true);
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(expectedCategory.getId());

        categoryGateway.create(expectedCategory);

        final var command = CreateGenreCommand.with(
            expectedName,
            expectedIsActive,
            asString(expectedCategories)
        );

        final var output = useCase.execute(command);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();

        final var actual = genreGateway.findById(GenreID.from(output.id()))
            .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.getId()).isEqualTo(GenreID.from(output.id()));
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).hasSize(1);
        assertThat(actual.getCategories()).containsAll(expectedCategories);
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNotNull();
        assertThat(actual.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidCommandWithoutCategories_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var output = useCase.execute(command);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();

        final var actual = genreGateway.findById(GenreID.from(output.id()))
            .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.getId()).isEqualTo(GenreID.from(output.id()));
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).isEmpty();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNotNull();
        assertThat(actual.getDeletedAt()).isNull();
    }

    @Test
    void givenAValidCommandWithIsNotActiveAndWithoutCategories_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Action";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var output = useCase.execute(command);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();

        final var actual = genreGateway.findById(GenreID.from(output.id()))
            .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.getId()).isEqualTo(GenreID.from(output.id()));
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedIsActive);
        assertThat(actual.getCategories()).isEmpty();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNotNull();
        assertThat(actual.getDeletedAt()).isNotNull();
    }

    @Test
    void givenAnInvalidEmptyName_whenCallsCreateGenre_shouldReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = Assertions.assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors()).hasSize(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void givenAnInvalidNullName_whenCallsCreateGenre_shouldReturnDomainException() {
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateGenreCommand.with(null, expectedIsActive, asString(expectedCategories));

        final var actual = Assertions.assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors()).hasSize(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
        final var expectedName = "Action";
        final var expectedIsActive = true;
        final var seriesId = Category.newCategory("Series", "Series", true);
        final var moviesId = Category.newCategory("Movies", "Movies", true);
        final var documentary = Category.newCategory("Documentary", "Documentary", true);
        final var expectedCategories = List.of(seriesId.getId(), moviesId.getId(), documentary.getId());
        final var expectedErrorMessage = "Some categories could not be found: %s, %s".formatted(seriesId.getId().getValue(), moviesId.getId().getValue());
        final var expectedErrorCount = 1;

        categoryGateway.create(documentary);

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = Assertions.assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors()).hasSize(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);

        verify(categoryGateway, atLeastOnce()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void givenAInvalidEmptyName_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var seriesId = Category.newCategory("Series", "Series", true);
        final var moviesId = Category.newCategory("Movies", "Movies", true);
        final var documentary = Category.newCategory("Documentary", "Documentary", true);
        final var expectedCategories = List.of(seriesId.getId(), moviesId.getId(), documentary.getId());
        final var expectedErrorMessage = "Some categories could not be found: %s, %s".formatted(seriesId.getId().getValue(), moviesId.getId().getValue());
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

        categoryGateway.create(documentary);

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actual = Assertions.assertThrows(
            NotificationException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(actual).isNotNull();
        assertThat(actual.getErrors()).hasSize(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);
        assertThat(actual.getErrors().get(1).message()).isEqualTo(expectedErrorMessageTwo);

        verify(categoryGateway, atLeastOnce()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
            .map(CategoryID::getValue)
            .toList();
    }

}
