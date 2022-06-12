package org.fullcycle.admin.catalog.application.category.create;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateCategoryUseCaseTest {

    @Test
    void givenAValidCommand_whenCallCreateCategory_thenReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var categoryGateway = Mockito.mock(CategoryGateway.class);
        when(categoryGateway.create(any()))
            .thenAnswer(returnsFirstArg());

        final var useCase = new DefaultCreateCategoryUseCase(categoryGateway);

        final var actual = useCase.execute(command);

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
}
