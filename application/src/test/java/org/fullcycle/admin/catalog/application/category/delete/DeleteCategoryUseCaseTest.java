package org.fullcycle.admin.catalog.application.category.delete;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @BeforeEach
    void cleanup() {
        reset(categoryGateway);
    }

    @Test
    void givenAValidId_whenCallDeleteCategory_shouldBeOk() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();

        doNothing().when(categoryGateway).deleteById(expectedId);

        final var command = DeleteCategoryCommand.with(expectedId.getValue());

        assertDoesNotThrow(() -> useCase.execute(command));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAInvalidId_whenCallDeleteCategory_shouldBeOk() {
        final var expectedId = CategoryID.unique();

        doNothing().when(categoryGateway).deleteById(expectedId);

        final var command = DeleteCategoryCommand.with(expectedId.getValue());

        assertDoesNotThrow(() -> useCase.execute(command));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedErrorMessage = "Gateway unexpected error";

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(categoryGateway).deleteById(expectedId);

        final var command = DeleteCategoryCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

}
