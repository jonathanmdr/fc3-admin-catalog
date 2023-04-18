package org.fullcycle.admin.catalog.infrastructure.configuration.usecases;

import org.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.create.DefaultCreateCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.delete.DeleteCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import org.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import org.fullcycle.admin.catalog.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import org.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMembersUseCase;
import org.fullcycle.admin.catalog.application.castmember.update.DefaultUpdateCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberUseCase;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CastMemberUseCaseConfiguration {

    private final CastMemberGateway castMemberGateway;

    public CastMemberUseCaseConfiguration(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Bean
    public ListCastMembersUseCase listCastMembersUseCase() {
        return new DefaultListCastMembersUseCase(castMemberGateway);
    }

    @Bean
    public GetCastMemberByIdUseCase getCastMemberByIdUseCase() {
        return new DefaultGetCastMemberByIdUseCase(castMemberGateway);
    }

    @Bean
    public CreateCastMemberUseCase createCastMemberUseCase() {
        return new DefaultCreateCastMemberUseCase(castMemberGateway);
    }

    @Bean
    public UpdateCastMemberUseCase updateCastMemberUseCase() {
        return new DefaultUpdateCastMemberUseCase(castMemberGateway);
    }

    @Bean
    public DeleteCastMemberUseCase deleteCastMemberUseCase() {
        return new DefaultDeleteCastMemberUseCase(castMemberGateway);
    }

}
