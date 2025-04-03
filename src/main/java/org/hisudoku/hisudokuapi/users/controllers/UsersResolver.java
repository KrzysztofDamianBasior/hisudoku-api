package org.hisudoku.hisudokuapi.users.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.users.dtos.*;
import org.hisudoku.hisudokuapi.users.models.MessageResponseModel;
import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;
import org.hisudoku.hisudokuapi.users.services.AdminActionsService;
import org.hisudoku.hisudokuapi.users.services.PublicActionsService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;

// @Validated annotation is a class-level annotation that we can use to tell Spring to validate parameters that are passed into a method of the annotated class.
@Validated
@Controller
@RequiredArgsConstructor
public class UsersResolver {
    private final PublicActionsService publicActionsService;
    private final AdminActionsService adminActionsService;

    @PreAuthorize("hasAuthority('user:read') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Query", field = "user")
    public UserModel getOneUser(
            @Argument @NotBlank String userId,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
//        DataFetchingFieldSelectionSet s = env.getSelectionSet();
//        if (s.contains("name"))

        return this.publicActionsService.findOneById(userId);
    }

    @PreAuthorize("hasAuthority('user:read') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Query", field = "userFeed")
    public UserFeedModel userFeed(
            @Argument String userCursor, // nullable: true
            @NotNull @Argument Integer usersLimit,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        return this.publicActionsService.userFeed(usersLimit, userCursor);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('admin:update')")
    @SchemaMapping(typeName = "Mutation", field = "updateOneUsername")
    public UserModel updateOneUsername(
            @Argument UpdateOneUsernameInput updateOneUsernameInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        //  @UseGuards(RolesGuard)
        //  @UseGuards(GqlAuthGuard)
        //  @Roles('Admin')

        return this.adminActionsService.updateOneUsername(updateOneUsernameInput);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('admin:update')")
    @SchemaMapping(typeName = "Mutation", field = "removeOneUser")
    public MessageResponseModel removeOne(
            @Argument RemoveOneInput removeOneInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        return this.adminActionsService.removeOne(removeOneInput);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('admin:update')")
    @SchemaMapping(typeName = "Mutation", field = "grantAdminPermissions")
    public UserModel grantAdminPermissions(
            @Argument GrantAdminPermissionsInput grantAdminPermissionsInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        return this.adminActionsService.grantOneAdminPermissions(grantAdminPermissionsInput);
    }

    // there is no need to define batch mapping for the list of users because they are retrieved as feed in the UserFeed model
    //    @BatchMapping(typeName = "UserFeed", field = "users")
    //    public Map<UserFeed, List<UserModel>> users(List<UserFeed> userFeeds) {
    //        return
    //    }
}
