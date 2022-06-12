package org.fullcycle.admin.catalog.application.category.create;

import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;

    @BeforeEach
    void cleanup() {
        reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallCreateCategory_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command).get();

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(categoryGateway, times(1))
            .create(argThat(category ->
                Objects.nonNull(category.getId())
                && Objects.equals(expectedName, category.getName())
                && Objects.equals(expectedDescription, category.getDescription())
                && Objects.equals(expectedIsActive, category.isActive())
                && Objects.nonNull(category.getCreatedAt())
                && Objects.nonNull(category.getUpdatedAt())
                && Objects.isNull(category.getDeletedAt())
            ));
    }

    @Test
    void givenAInvalidName_whenCallCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var actual = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallCreateCategory_thenReturnInactiveCategoryId() {
        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var actual = useCase.execute(command).get();

        assertNotNull(actual);
        assertNotNull(actual.id());

        verify(categoryGateway, times(1))
            .create(argThat(category ->
                Objects.nonNull(category.getId())
                    && Objects.equals(expectedName, category.getName())
                    && Objects.equals(expectedDescription, category.getDescription())
                    && Objects.equals(expectedIsActive, category.isActive())
                    && Objects.nonNull(category.getCreatedAt())
                    && Objects.nonNull(category.getUpdatedAt())
                    && Objects.nonNull(category.getDeletedAt())
            ));
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsUnexpectedException_thenShouldReturnAException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway unexpected error";
        final var expectedErrorCount = 1;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any()))
            .thenThrow(new IllegalStateException("Gateway unexpected error"));

        final var actual = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, actual.getErrors().size());
        actual.firstError()
            .ifPresent(error -> assertEquals(expectedErrorMessage, error.message()));

        verify(categoryGateway, times(1))
            .create(argThat(category ->
                Objects.nonNull(category.getId())
                    && Objects.equals(expectedName, category.getName())
                    && Objects.equals(expectedDescription, category.getDescription())
                    && Objects.equals(expectedIsActive, category.isActive())
                    && Objects.nonNull(category.getCreatedAt())
                    && Objects.nonNull(category.getUpdatedAt())
                    && Objects.isNull(category.getDeletedAt())
            ));
    }

}
