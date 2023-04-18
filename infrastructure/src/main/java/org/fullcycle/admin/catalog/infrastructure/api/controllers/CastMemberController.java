package org.fullcycle.admin.catalog.infrastructure.api.controllers;

import org.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberCommand;
import org.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.delete.DeleteCastMemberCommand;
import org.fullcycle.admin.catalog.application.castmember.delete.DeleteCastMemberUseCase;
import org.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdCommand;
import org.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import org.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMembersCommand;
import org.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMembersUseCase;
import org.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberCommand;
import org.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberUseCase;
import org.fullcycle.admin.catalog.domain.castmember.CastMemberType;
import org.fullcycle.admin.catalog.domain.pagination.Pagination;
import org.fullcycle.admin.catalog.infrastructure.api.CastMemberAPI;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.GetCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.ListCastMembersResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberResponse;
import org.fullcycle.admin.catalog.infrastructure.castmember.presenters.CastMemberApiPresenter;
import org.fullcycle.admin.catalog.infrastructure.utils.UriUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final ListCastMembersUseCase listCastMembersUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;

    public CastMemberController(
        final ListCastMembersUseCase listCastMembersUseCase,
        final GetCastMemberByIdUseCase getCastMemberByIdUseCase,
        final CreateCastMemberUseCase createCastMemberUseCase,
        final UpdateCastMemberUseCase updateCastMemberUseCase,
        final DeleteCastMemberUseCase deleteCastMemberUseCase
    ) {
        this.listCastMembersUseCase = Objects.requireNonNull(listCastMembersUseCase, "'ListCastMembersUseCase' cannot be null");
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase, "'GetCastMemberByIdUseCase' cannot be null");
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase, "'CreateCastMemberUseCase' cannot be null");
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase, "'UpdateCastMemberUseCase' cannot be null");
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase, "'DeleteCastMemberUseCase' cannot be null");
    }

    @Override
    public Pagination<ListCastMembersResponse> listCastMembers(
        final String search,
        final int page,
        final int perPage,
        final String sort,
        final String direction
    ) {
        final var command = ListCastMembersCommand.with(
            page,
            perPage,
            search,
            sort,
            direction
        );

        return this.listCastMembersUseCase.execute(command)
            .map(CastMemberApiPresenter::present);
    }

    @Override
    public GetCastMemberResponse getCastMemberById(final String id) {
        final var command = GetCastMemberByIdCommand.with(id);
        return CastMemberApiPresenter.present(this.getCastMemberByIdUseCase.execute(command));
    }

    @Override
    public ResponseEntity<CreateCastMemberResponse> createCastMember(final CreateCastMemberRequest input) {
        final var command = CreateCastMemberCommand.with(
            input.name(),
            CastMemberType.valueOf(input.type())
        );

        final var output = this.createCastMemberUseCase.execute(command);
        return ResponseEntity.created(UriUtils.buildAndExpandResourceId(output.id()))
            .body(CastMemberApiPresenter.present(output));
    }

    @Override
    public ResponseEntity<UpdateCastMemberResponse> updateCastMember(final String id, final UpdateCastMemberRequest input) {
        final var command = UpdateCastMemberCommand.with(
            id,
            input.name(),
            CastMemberType.valueOf(input.type())
        );

        final var output = this.updateCastMemberUseCase.execute(command);
        return ResponseEntity.ok(CastMemberApiPresenter.present(output));
    }

    @Override
    public void deleteCastMemberById(final String id) {
        final var command = DeleteCastMemberCommand.with(id);
        this.deleteCastMemberUseCase.execute(command);
    }

}
