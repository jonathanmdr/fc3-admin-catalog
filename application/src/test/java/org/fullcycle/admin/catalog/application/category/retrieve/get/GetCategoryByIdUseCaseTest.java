package org.fullcycle.admin.catalog.application.category.retrieve.get;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @BeforeEach
    void cleanup() {
        reset(categoryGateway);
    }

    @Test
    void givenAValidId_whenCallGetCategoryById_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        when(categoryGateway.findById(expectedId))
            .thenReturn(Optional.of(Category.with(category)));

        final var command = GetCategoryByIdCommand.with(expectedId.getValue());

        final var actual = useCase.execute(command);

        assertEquals(expectedId, actual.id());
        assertEquals(expectedName, actual.name());
        assertEquals(expectedDescription, actual.description());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(category.getCreatedAt(), actual.createdAt());
        assertEquals(category.getUpdatedAt(), actual.updatedAt());
        assertEquals(category.getDeletedAt(), actual.deletedAt());
    }

    @Test
    void givenAInvalidId_whenCallGetCategoryById_thenReturnNotFoundException() {
        final var expectedId = CategoryID.unique();
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());

        when(categoryGateway.findById(expectedId))
            .thenReturn(Optional.empty());

        final var command = GetCategoryByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            DomainException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(categoryGateway, times(1)).findById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var expectedId = CategoryID.unique();
        final var expectedErrorMessage = "Gateway unexpected error";

        when(categoryGateway.findById(expectedId))
            .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var command = GetCategoryByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(categoryGateway, times(1)).findById(expectedId);
    }

}
