package org.fullcycle.admin.catalog.infrastructure.configuration.usecases;

import org.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.create.DefaultCreateCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.delete.DefaultDeleteCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.get.GetCategoryByIdUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.list.DefaultListCategoriesUseCase;
import org.fullcycle.admin.catalog.application.category.retrieve.list.ListCategoriesUseCase;
import org.fullcycle.admin.catalog.application.category.update.DefaultUpdateCategoryUseCase;
import org.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import org.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CategoryUseCaseConfiguration {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfiguration(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }

}
