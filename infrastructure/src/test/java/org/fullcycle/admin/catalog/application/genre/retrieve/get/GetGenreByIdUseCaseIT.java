package org.fullcycle.admin.catalog.application.genre.retrieve.get;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.genre.Genre;
import org.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@IntegrationTest
class GetGenreByIdUseCaseIT {

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GetGenreByIdUseCase useCase;

    @Test
    void givenAValidGenreId_whenCallsGetGenreById_shouldReturnsGenre() {
        final var expected = Genre.newGenre("Action", true);
        final var expectedId = expected.getId();
        final var expectedFirstCategory = Category.newCategory("Series", "Series", true);
        final var expectedSecondCategory = Category.newCategory("Movies", "Movies", true);
        final var categories = List.of(
            expectedFirstCategory.getId(),
            expectedSecondCategory.getId()
        );
        expected.addCategories(categories);

        categoryGateway.create(expectedFirstCategory);
        categoryGateway.create(expectedSecondCategory);
        genreGateway.create(expected);

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = useCase.execute(command);

        assertThat(actual.id()).isEqualTo(expected.getId());
        assertThat(actual.name()).isEqualTo(expected.getName());
        assertThat(actual.isActive()).isEqualTo(expected.isActive());
        assertThat(actual.categories()).containsAll(expected.getCategories());
        assertThat(actual.createdAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.updatedAt()).isEqualTo(expected.getUpdatedAt());
        assertThat(actual.deletedAt()).isEqualTo(expected.getDeletedAt());

        verify(genreGateway, atMostOnce()).findById(expectedId);
    }

    @Test
    void givenAGenreWithoutCategoriesAndAValidGenreId_whenCallsGetGenreById_shouldReturnsGenre() {
        final var expected = Genre.newGenre("Action", true);
        final var expectedId = expected.getId();

        genreGateway.create(expected);

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = useCase.execute(command);

        assertThat(actual.id()).isEqualTo(expected.getId());
        assertThat(actual.name()).isEqualTo(expected.getName());
        assertThat(actual.isActive()).isEqualTo(expected.isActive());
        assertThat(actual.categories()).isEmpty();
        assertThat(actual.createdAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.updatedAt()).isEqualTo(expected.getUpdatedAt());
        assertThat(actual.deletedAt()).isEqualTo(expected.getDeletedAt());

        verify(genreGateway, atMostOnce()).findById(expectedId);
    }

    @Test
    void givenAnInvalidGenreId_whenCallsGetGenreById_shouldReturnsNotFoundException() {
        final var expectedId = GenreID.unique();
        final var expectedErrorMessage = "Genre with ID %s was not found".formatted(expectedId.getValue());

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(genreGateway, atMostOnce()).findById(expectedId);
    }

    @Test
    void givenAValidGenreId_whenCallsGetGenreByIdAndGatewayThrowsUnexpectedException_shouldReturnsException() {
        final var expected = Genre.newGenre("Action", true);
        final var expectedId = expected.getId();
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(genreGateway).findById(any());

        final var command = GetGenreByIdCommand.with(expectedId.getValue());
        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command),
            expectedErrorMessage
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(genreGateway, atMostOnce()).findById(expectedId);
    }

}
