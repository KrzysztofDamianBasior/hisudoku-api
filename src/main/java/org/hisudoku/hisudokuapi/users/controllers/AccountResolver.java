package org.hisudoku.hisudokuapi.users.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Locale;
import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.sudokus.models.SudokuFeedModel;
import org.hisudoku.hisudokuapi.sudokus.services.SudokusService;
import org.hisudoku.hisudokuapi.users.dtos.*;
import org.hisudoku.hisudokuapi.users.models.AccountModel;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.models.MessageResponseModel;
import org.hisudoku.hisudokuapi.users.services.UserActionsService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;

// ref: https://docs.spring.io/spring-graphql/docs/1.0.4/reference/html/
// @Argument("inputName").
//	The @Argument annotation does not have a "required" flag, nor the option to specify a default value. Both of these can be specified at the GraphQL schema level and are enforced by GraphQL Java. If binding fails, a BindException is raised with binding issues accumulated as field errors where the field of each error is the argument path where the issue occurred.

//Spring detects @Controller beans and registers their annotated handler methods ( @QueryMapping or @SchemaMapping) as DataFetcher s (also known as a resolver). A resolver/DataFetcher is a function that’s responsible for populating the data for a single field in your GraphQL schema. Resolvers provide the instructions for turning a GraphQL operation (a query, mutation, or subscription) into data.
@Validated
@Controller
@RequiredArgsConstructor
public class AccountResolver {
    private final UserActionsService userActionsService;
    private final SudokusService sudokusService;

    // no auth required
    @PreAuthorize("permitAll()")
    @SchemaMapping(typeName = "Mutation", field = "forgotPassword")
    public MessageResponseModel forgotPassword(
            @Argument @Valid ForgotPasswordInput forgotPasswordInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            Locale locale
    ) {
        return userActionsService.requestUpdatePasswordByJWT(forgotPasswordInput, locale.getLanguage());
    }

    // no auth required
    @PreAuthorize("permitAll()")
    @SchemaMapping(typeName = "Mutation", field = "activateEmail")
    public AccountModel activateEmail(
            @Argument @Valid ActivateEmailInput activateEmailInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            Locale locale
    ) {
        return userActionsService.activateEmail(activateEmailInput);
    }

    @PreAuthorize("isAuthenticated()")
    @SchemaMapping(typeName = "Query", field = "myAccount")
    public AccountModel getAccountInfo(
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return this.userActionsService.findOneById(principal.getId());
    }

    @PreAuthorize("hasAuthority('user:update') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Mutation", field = "updateMyUsername")
    public AccountModel updateUsername(
            @Argument @Valid UpdateMyUsernameInput updateMyUsernameInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        return this.userActionsService.updateOneUsername(principal, updateMyUsernameInput);
    }

    @PreAuthorize("hasAuthority('user:update') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Mutation", field = "updateMyEmail")
    public MessageResponseModel updateEmail(
            @Argument @Valid UpdateMyEmailInput updateMyEmailInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal,
            Locale locale
            // Principal principal,
            // Authentication authentication,
    ) {
        return this.userActionsService.requestUpdateEmail(principal, updateMyEmailInput, locale.getLanguage());
    }

    @PreAuthorize("isAuthenticated()")
    @SchemaMapping(typeName = "Mutation", field = "updateMyPassword")
    public AccountModel updatePassword(
            @Argument @Valid UpdateMyPasswordInput updateMyPasswordInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        return this.userActionsService.updatePassword(principal, updateMyPasswordInput.getNewPassword());
    }

    @PreAuthorize("isAuthenticated()")
    @SchemaMapping(typeName = "Mutation", field = "removeMyAccount")
    public MessageResponseModel removeAccount(
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        return this.userActionsService.removeOne(principal);
    }

    // NESTED QUIRES ---------------------------------------------------------------------------------------------------
    @SchemaMapping(typeName = "AccountModel", field = "createdSudokus")
    public SudokuFeedModel createdSudokus(
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            AccountModel parent,
            @Argument("sudokuCursor") @Size(min = 24, message = "{validation.mongo-id.size.too-short}") @Size(max = 24, message = "{validation.mongo-id.size.too-long}") String sudokuCursor,
            @Argument("sudokusLimit") @Max(value = 50, message = "{validation.sudokus-limit}") @NotNull int sudokusLimit
            // @AuthenticationPrincipal HSUserPrincipal principal,
            // Principal principal,
            // Authentication authentication,
    ) {
        return this.sudokusService.findManyByAuthor(parent.getId(), sudokusLimit, sudokuCursor);
    }
}
