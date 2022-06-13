package org.fullcycle.admin.catalog.application.category.create;

import org.fullcycle.admin.catalog.IntegrationTest;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@IntegrationTest
class CreateCategoryUseCaseIT {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CreateCategoryUseCase useCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallCreateCategory_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actual = useCase.execute(command).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(actual);
        assertNotNull(actual.id());

        final var category = categoryRepository.findById(actual.id().getValue()).get();

        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedIsActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNull(category.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actual = useCase.execute(command).getLeft();

        assertEquals(0, categoryRepository.count());

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        verify(categoryGateway, Mockito.times(0)).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallCreateCategory_thenReturnInactiveCategoryId() {
        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var actual = useCase.execute(command).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(actual);
        assertNotNull(actual.id());

        final var category = categoryRepository.findById(actual.id().getValue()).get();

        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedIsActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNotNull(category.getDeletedAt());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsUnexpectedException_thenShouldReturnAException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway unexpected error";
        final var expectedErrorCount = 1;

        doThrow(new IllegalStateException(expectedErrorMessage))
            .when(categoryGateway).create(any());

        assertEquals(0, categoryRepository.count());

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var actual = useCase.execute(command).getLeft();

        assertEquals(0, categoryRepository.count());

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        verify(categoryGateway, Mockito.times(1)).create(any());
    }

}
