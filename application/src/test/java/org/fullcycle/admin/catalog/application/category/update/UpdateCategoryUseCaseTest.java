package org.fullcycle.admin.catalog.application.category.update;

import org.fullcycle.admin.catalog.domain.category.Category;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

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

}
