package org.fullcycle.admin.catalog.application.genre.update;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@IntegrationTest
class UpdateGenreUseCaseIT {

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private DefaultUpdateGenreUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("Bla", true);
        final var createdGenre = genreGateway.create(genre);

        assertThat(createdGenre.getId()).isEqualTo(genre.getId());
        assertThat(createdGenre.getName()).isEqualTo(genre.getName());
        assertThat(createdGenre.isActive()).isEqualTo(genre.isActive());
        assertThat(createdGenre.getCategories()).isEmpty();
        assertThat(createdGenre.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(createdGenre.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(createdGenre.getDeletedAt()).isEqualTo(genre.getDeletedAt());

        final var expectedGenreId = genre.getId();
        final var expectedName = "Action";
        final var expectedActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            expectedName,
            expectedActive,
            asString(expectedCategories)
        );

        final var output = useCase.execute(command);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(expectedGenreId.getValue());

        final var actual = genreGateway.findById(GenreID.from(output.id()))
            .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.getId()).isEqualTo(genre.getId());
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedActive);
        assertThat(actual.getCategories()).isEmpty();
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isEqualTo(genre.getDeletedAt());

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(genreGateway, atLeastOnce()).update(any());
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("Action", true);

        final var createdGenre = genreGateway.create(genre);

        assertThat(createdGenre.getId()).isEqualTo(genre.getId());
        assertThat(createdGenre.getName()).isEqualTo(genre.getName());
        assertThat(createdGenre.isActive()).isEqualTo(genre.isActive());
        assertThat(createdGenre.getCategories()).isEmpty();
        assertThat(createdGenre.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(createdGenre.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(createdGenre.getDeletedAt()).isEqualTo(genre.getDeletedAt());

        final var expectedGenreId = genre.getId();
        final var expectedName = "Action";
        final var expectedActive = false;
        final var expectedCategories = Collections.<CategoryID>emptyList();

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            expectedName,
            expectedActive,
            asString(expectedCategories)
        );

        assertThat(genre.isActive()).isTrue();
        assertThat(genre.getDeletedAt()).isNull();

        final var output = useCase.execute(command);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(expectedGenreId.getValue());

        final var actual = genreGateway.findById(GenreID.from(output.id()))
            .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.getId()).isEqualTo(genre.getId());
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedActive);
        assertThat(actual.getCategories()).isEmpty();
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isNotNull();

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(genreGateway, atLeastOnce()).update(any());
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var genre = Genre.newGenre("Action", true);

        final var createdGenre = genreGateway.create(genre);

        assertThat(createdGenre.getId()).isEqualTo(genre.getId());
        assertThat(createdGenre.getName()).isEqualTo(genre.getName());
        assertThat(createdGenre.isActive()).isEqualTo(genre.isActive());
        assertThat(createdGenre.getCategories()).isEmpty();
        assertThat(createdGenre.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(createdGenre.getUpdatedAt()).isEqualTo(genre.getUpdatedAt());
        assertThat(createdGenre.getDeletedAt()).isEqualTo(genre.getDeletedAt());

        final var expectedGenreId = genre.getId();
        final var expectedName = "Action";
        final var expectedActive = true;
        final var expectedCategoryOne = Category.newCategory("Movies", "Movies", true);
        final var expectedCategoryTwo = Category.newCategory("Documentary", "Documentary", true);
        final var expectedCategories = List.of(
            expectedCategoryOne.getId(),
            expectedCategoryTwo.getId()
        );

        categoryGateway.create(expectedCategoryOne);
        categoryGateway.create(expectedCategoryTwo);

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            expectedName,
            expectedActive,
            asString(expectedCategories)
        );

        assertThat(genre.isActive()).isTrue();
        assertThat(genre.getDeletedAt()).isNull();

        final var output = useCase.execute(command);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(expectedGenreId.getValue());

        final var actual = genreGateway.findById(GenreID.from(output.id()))
            .orElseThrow(() -> new IllegalStateException("Expected genre cannot be null"));

        assertThat(actual.getId()).isEqualTo(genre.getId());
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.isActive()).isEqualTo(expectedActive);
        assertThat(actual.getCategories()).containsAll(expectedCategories);
        assertThat(actual.getCreatedAt()).isEqualTo(genre.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isAfter(genre.getUpdatedAt());
        assertThat(actual.getDeletedAt()).isNull();

        verify(categoryGateway, atLeastOnce()).existsByIds(eq(expectedCategories));
        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(genreGateway, atLeastOnce()).update(any());
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnNotificationException() {
        final var genre = Genre.newGenre("Action", true);

        genreGateway.create(genre);

        final var expectedGenreId = genre.getId();
        final var expectedActive = true;
        final var expectedCategories = Collections.<CategoryID>emptyList();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            null,
            expectedActive,
            asString(expectedCategories)
        );

        final var actual = assertThrows(
            DomainException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertThat(actual.getErrors()).hasSize(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedErrorMessage);

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).update(any());
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotificationException() {
        final var genre = Genre.newGenre("Action", true);

        genreGateway.create(genre);

        final var expectedGenreId = genre.getId();
        final var expectedActive = true;
        final var expectedValidCategory = Category.newCategory("Movies", "Movies", true);
        final var expectedInvalidCategoryId = CategoryID.unique();
        final var expectedCategories = List.of(
            expectedValidCategory.getId(),
            expectedInvalidCategoryId
        );
        final var expectedErrorCount = 2;
        final var expectedFirstErrorMessage = "Some categories could not be found: %s".formatted(expectedInvalidCategoryId.getValue());
        final var expectedSecondErrorMessage = "'name' should not be null";

        final var command = UpdateGenreCommand.with(
            expectedGenreId.getValue(),
            null,
            expectedActive,
            asString(expectedCategories)
        );

        categoryGateway.create(expectedValidCategory);

        final var actual = assertThrows(
            DomainException.class,
            () -> useCase.execute(command)
        );

        assertThat(actual.getErrors()).hasSize(expectedErrorCount);
        assertThat(actual.getErrors().get(0).message()).isEqualTo(expectedFirstErrorMessage);
        assertThat(actual.getErrors().get(1).message()).isEqualTo(expectedSecondErrorMessage);

        verify(genreGateway, atLeastOnce()).findById(eq(expectedGenreId));
        verify(categoryGateway, atLeastOnce()).existsByIds(eq(expectedCategories));
        verify(genreGateway, never()).update(any());
    }

    private List<String> asString(final List<CategoryID> categoryIDS) {
        return categoryIDS.stream()
            .map(CategoryID::getValue)
            .toList();
    }

}
