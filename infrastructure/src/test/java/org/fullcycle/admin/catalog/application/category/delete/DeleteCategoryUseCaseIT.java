package org.fullcycle.admin.catalog.application.category.delete;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class DeleteCategoryUseCaseIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DeleteCategoryUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallDeleteCategory_shouldBeOk() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();

        categoryRepository.save(CategoryJpaEntity.from(category));

        final var command = DeleteCategoryCommand.with(expectedId.getValue());

        assertEquals(1, categoryRepository.count());

        useCase.execute(command);

        assertEquals(0, categoryRepository.count());

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAInvalidId_whenCallDeleteCategory_shouldBeOk() {
        final var expectedId = CategoryID.unique();

        assertEquals(0, categoryRepository.count());

        final var command = DeleteCategoryCommand.with(expectedId.getValue());

        useCase.execute(command);

        assertEquals(0, categoryRepository.count());

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedErrorMessage = "Gateway unexpected error";

        assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(categoryGateway).deleteById(expectedId);

        final var command = DeleteCategoryCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertEquals(1, categoryRepository.count());

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

}
