package org.fullcycle.admin.catalog.application.category.update;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.fullcycle.admin.catalog.domain.utils.IdentifierUtils;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class UpdateCategoryUseCaseIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UpdateCategoryUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallUpdateCategory_thenReturnCategoryId() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        final var actual = useCase.execute(command).get();

        final var categoryUpdated =  categoryRepository.findById(category.getId().getValue());

        categoryUpdated.ifPresent(actualCategory -> {
            assertEquals(actual.id(), actualCategory.getId());
            assertEquals(expectedName, actualCategory.getName());
            assertEquals(expectedDescription, actualCategory.getDescription());
            assertEquals(expectedIsActive, actualCategory.isActive());
            assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
            assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
            assertNull(actualCategory.getDeletedAt());
        });
        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(any());
    }

    @Test
    void givenAInvalidName_whenCallUpdateCategory_thenShouldReturnDomainException() {
        final var category = Category.newCategory("Bla", "Bla", true);
        final var expectedId = category.getId();
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

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

        assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        assertTrue(category.isActive());
        assertNull(category.getDeletedAt());

        final var actual = useCase.execute(command).get();

        final var categoryUpdated =  categoryRepository.findById(category.getId().getValue());

        categoryUpdated.ifPresent(actualCategory -> {
            assertEquals(actual.id(), actualCategory.getId());
            assertEquals(expectedName, actualCategory.getName());
            assertEquals(expectedDescription, actualCategory.getDescription());
            assertEquals(expectedIsActive, actualCategory.isActive());
            assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
            assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
            assertNotNull(actualCategory.getDeletedAt());
        });
        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(any());
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

        assertEquals(0, categoryRepository.count());

        categoryRepository.save(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var command = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(categoryGateway).update(any());

        final var actual = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        final var categorySaved =  categoryRepository.findById(category.getId().getValue());

        categorySaved.ifPresent(actualCategory -> {
            assertEquals(expectedId.getValue(), actualCategory.getId());
            assertEquals(category.getName(), actualCategory.getName());
            assertEquals(category.getDescription(), actualCategory.getDescription());
            assertEquals(category.isActive(), actualCategory.isActive());
            assertEquals(category.getCreatedAt(), actualCategory.getCreatedAt());
            assertFalse(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
            assertNull(actualCategory.getDeletedAt());
        });
        verify(categoryGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).update(any());
    }

    @Test
    void givenACommandWithInvalidId_whenCallUpdateCategory_thenReturnNotFoundException() {
        final var expectedId = IdentifierUtils.unique();
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId);
        final var expectedErrorCount = 0;

        assertEquals(0, categoryRepository.count());

        final var command = UpdateCategoryCommand.with(
            expectedId,
            expectedName,
            expectedDescription,
            expectedIsActive
        );

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
