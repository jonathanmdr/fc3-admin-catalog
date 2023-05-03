package org.fullcycle.admin.catalog.application.category.update;

import org.fullcycle.admin.catalog.application.UseCaseTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateCategoryUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(
            categoryGateway
        );
    }

    @Test
    void givenAValidCommand_whenCallUpdateCategory_thenReturnCategoryId() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(Category.with(category)));
        when(categoryGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command).get();

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1))
            .update(argThat(categoryUpdate ->
                Objects.equals(expectedId, categoryUpdate.getId())
                    && Objects.equals(expectedName, categoryUpdate.getName())
                    && Objects.equals(expectedDescription, categoryUpdate.getDescription())
                    && Objects.equals(expectedIsActive, categoryUpdate.isActive())
                    && Objects.equals(category.getCreatedAt(), categoryUpdate.getCreatedAt())
                    && category.getUpdatedAt().isBefore(categoryUpdate.getUpdatedAt())
                    && Objects.isNull(categoryUpdate.getDeletedAt())
            ));
    }

    @Test
    void givenAInvalidName_whenCallUpdateCategory_thenShouldReturnDomainException() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            null,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(Category.with(category)));

        final var actual = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactivateCommand_whenCallUpdateCategory_thenReturnInactiveCategoryId() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(Category.with(category)));
        when(categoryGateway.update(any()))
            .thenAnswer(returnsFirstArg());

        assertTrue(category.isActive());
        assertNull(category.getDeletedAt());

        useCase.execute(command).get();

        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1))
            .update(argThat(categoryUpdate ->
                Objects.equals(expectedId, categoryUpdate.getId())
                    && Objects.equals(expectedName, categoryUpdate.getName())
                    && Objects.equals(expectedDescription, categoryUpdate.getDescription())
                    && Objects.equals(expectedIsActive, categoryUpdate.isActive())
                    && Objects.equals(category.getCreatedAt(), categoryUpdate.getCreatedAt())
                    && category.getUpdatedAt().isBefore(categoryUpdate.getUpdatedAt())
                    && Objects.nonNull(categoryUpdate.getDeletedAt())
            ));
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsUnexpectedException_thenShouldReturnAException() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway unexpected error";
        final var expectedErrorCount = 1;

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId)))
            .thenReturn(Optional.of(Category.with(category)));
        when(categoryGateway.update(any()))
            .thenThrow(new IllegalStateException("Gateway unexpected error"));

        final var actual = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        verify(categoryGateway, times(1))
            .update(argThat(categoryUpdate ->
                Objects.nonNull(categoryUpdate.getId())
                    && Objects.equals(expectedName, categoryUpdate.getName())
                    && Objects.equals(expectedDescription, categoryUpdate.getDescription())
                    && Objects.equals(expectedIsActive, categoryUpdate.isActive())
                    && Objects.nonNull(categoryUpdate.getCreatedAt())
                    && Objects.nonNull(categoryUpdate.getUpdatedAt())
                    && Objects.isNull(categoryUpdate.getDeletedAt())
            ));
    }

    @Test
    void givenACommandWithInvalidId_whenCallUpdateCategory_thenReturnNotFoundException() {
        final var expectedId = IdentifierUtils.unique();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId);
        final var expectedErrorCount = 0;

        final var command = UpdateCategoryCommand.with(
            expectedId,
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(CategoryID.from(expectedId))))
            .thenReturn(Optional.empty());

        final var actual  = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorCount, actual.getErrors().size());
        assertEquals(expectedErrorMessage, actual.getMessage());

        verify(categoryGateway, times(1)).findById(eq(CategoryID.from(expectedId)));
        verify(categoryGateway, times(0)).update(any());
    }

}
