package org.fullcycle.admin.catalog.application.category.retrieve.get;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class GetCategoryByIdUseCaseIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallGetCategoryById_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId();

        assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var command = GetCategoryByIdCommand.with(expectedId.getValue());

        final var actual = useCase.execute(command);

        assertEquals(expectedId, actual.id());
        assertEquals(expectedName, actual.name());
        assertEquals(expectedDescription, actual.description());
        assertEquals(expectedIsActive, actual.isActive());
        assertEquals(category.getCreatedAt(), actual.createdAt());
        assertEquals(category.getUpdatedAt(), actual.updatedAt());
        assertEquals(category.getDeletedAt(), actual.deletedAt());
        verify(categoryGateway, times(1)).findById(eq(expectedId));
    }

    @Test
    void givenAInvalidId_whenCallGetCategoryById_thenReturnNotFoundException() {
        final var expectedId = CategoryID.unique();
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());

        assertEquals(0, categoryRepository.count());

        final var command = GetCategoryByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            NotFoundException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(categoryGateway, times(1)).findById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsUnexpectedException_thenReturnException() {
        final var expectedId = CategoryID.unique();
        final var expectedErrorMessage = "Gateway unexpected error";

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(categoryGateway).findById(expectedId);

        final var command = GetCategoryByIdCommand.with(expectedId.getValue());

        final var actual = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(command)
        );

        assertEquals(expectedErrorMessage, actual.getMessage());
        verify(categoryGateway, times(1)).findById(expectedId);
    }

}
