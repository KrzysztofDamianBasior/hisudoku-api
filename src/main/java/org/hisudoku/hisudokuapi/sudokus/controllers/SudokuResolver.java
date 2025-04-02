package org.hisudoku.hisudokuapi.sudokus.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hisudoku.hisudokuapi.sudokus.dtos.*;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuFeedModel;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuModel;
import org.hisudoku.hisudokuapi.sudokus.services.SudokusService;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.models.MessageResponseModel;
import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;

import lombok.RequiredArgsConstructor;

@Validated
@Controller
@RequiredArgsConstructor
public class SudokuResolver {
    private final SudokusService sudokusService;

    @PreAuthorize("permitAll()")
    @SchemaMapping(typeName = "Query", field = "sudokuFeed")
    public SudokuFeedModel sudokuFeed(
            // By default, input arguments in GraphQL are nullable and optional, which means an argument can be set to the null literal, or not provided at all.
            @Argument @Size(min = 24, message = "{validation.mongo-id.size.too-short}") @Size(max = 24, message = "{validation.mongo-id.size.too-long}") String sudokuCursor, // nullable: true
            //    @Max(value = 100, message = "You can request data from up to {value} sudoku{value > 1 ? 's' : ''}")
            @Argument @Max(value = 50, message = "{validation.sudokus-limit}") @NotNull Integer sudokusLimit,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        // graphQLContext.put("sudokuFeedArgs", sudokuFeedArgs);
        // @ContextValue(name = "sudokuFeedArgs") SudokuFeedArgs sudokuFeedArgs -allows to access Query argument from the OutputType level

        // env.getVariables().get("sudokusLimit");
        // env.getVariables().get("sudokuCursor");

        // DataFetchingFieldSelectionSet s = env.getSelectionSet();
        // if (s.contains("sudokuCursor"))
        return this.sudokusService.sudokuFeed(sudokusLimit, sudokuCursor);
    }

    @PreAuthorize("permitAll()")
    @SchemaMapping(typeName = "Query", field = "sudoku")
    public SudokuModel getOne(
            @Argument @Size(min = 24, message = "{validation.mongo-id.size.too-short}") @Size(max = 24, message = "{validation.mongo-id.size.too-long}") @NotBlank String sudokuId,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        // graphQLContext.put("findSudokuArgs", findSudokuArgs);
        // @ContextValue(name = "findSudokuArgs") FindSudokuArgs findSudokuArgs -allows to access Query argument from the OutputType level

        // env.getVariables().get("sudokuId");

        // DataFetchingFieldSelectionSet s = env.getSelectionSet();
        // if (s.contains("author"))

        return this.sudokusService.findOne(sudokuId);
    }

    @PreAuthorize("hasAuthority('user:create') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Mutation", field = "createSudoku")
    public SudokuModel createSudoku(
            @Argument @Valid AddSudokuInput addSudokuInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        // graphQLContext.put("addSudokuInput", addSudokuInput);
        // @ContextValue(name = "addSudokuInput") AddSudokuInput addSudokuInput -allows to access Query argument from the OutputType level

        // env.getVariables().get("content");

        // DataFetchingFieldSelectionSet s = env.getSelectionSet();
        // if (s.contains("author"))

        return this.sudokusService.addSudoku(principal, addSudokuInput);
    }

    @PreAuthorize("hasAuthority('user:update') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Mutation", field = "updateSudoku")
    public SudokuModel updateSudoku(
            @Argument @Valid UpdateSudokuInput updateSudokuInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        // graphQLContext.put("updateSudokuInput", updateSudokuInput);
        // @ContextValue(name = "updateSudokuInput") UpdateSudokuInput updateSudokuInput -allows to access Mutation argument from the OutputType level

        // env.getVariables().get("sudokuId");
        // env.getVariables().get("sudokuContent");

        // DataFetchingFieldSelectionSet s = env.getSelectionSet();
        // if (s.contains("author"))

        return this.sudokusService.updateSudokuContent(principal, updateSudokuInput);
    }

    @PreAuthorize("hasAuthority('user:delete') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Mutation", field = "removeSudoku")
    public MessageResponseModel removeSudoku(
            @Argument @Valid RemoveSudokuInput removeSudokuInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        // graphQLContext.put("removeSudokuInput", removeSudokuInput);
        // @ContextValue(name = "removeSudokuInput") RemoveSudokuInput removeSudokuInput -allows to access Mutation argument from the OutputType level

        // env.getVariables().get("sudokuId");

        // DataFetchingFieldSelectionSet s = env.getSelectionSet();
        // if (s.contains("author"))

        return this.sudokusService.remove(principal, removeSudokuInput);
    }

    @PreAuthorize("hasAuthority('user:update') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @SchemaMapping(typeName = "Mutation", field = "toggleFavoriteSudoku")
    public SudokuModel toggleFavorite(
            @Argument @Valid ToggleFavouriteSudokuInput toggleFavouriteSudokuInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            @AuthenticationPrincipal HSUserPrincipal principal
            // Principal principal,
            // Authentication authentication,
    ) {
        // graphQLContext.put("toggleFavouriteSudokuInput", toggleFavouriteSudokuInput);
        // @ContextValue(name = "toggleFavouriteSudokuInput") ToggleFavouriteSudokuInput toggleFavouriteSudokuInput -allows to access Mutation argument from the OutputType level

        // env.getVariables().get("sudokuId");

        // DataFetchingFieldSelectionSet s = env.getSelectionSet();
        // if (s.contains("author"))

        return this.sudokusService.toggleFavorite(principal, toggleFavouriteSudokuInput);
    }

    // nested ----------------------------------------------------------------------------------------------------------

    @SchemaMapping(typeName = "SudokuModel", field = "author")
    public UserModel author(
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            SudokuModel parent
    ) {
        //  In some situations, graphql outperforms RESTful APIs, for example, allowing multiple queries in a single request, querying nested resources

        // env.getExecutionStepInfo().getParent().getArgument("id")
        // env.getExecutionStepInfo().getParent();
        // env.getVariables().get("sudokuId");

        return this.sudokusService.findSudokuAuthor(parent.getId());
    }

    //    @BatchMapping(typeName = "SudokuModel", field = "author")
    //    public Map<SudokuModel, UserModel> authors(List<SudokuModel> sudokus) {
    //        return this.sudokusService.findSudokusAuthors(sudokus);
    //    }

    @SchemaMapping(typeName = "SudokuModel", field = "favouritedBy")
    public UserFeedModel favouritedBy(
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            SudokuModel parent,
            @Argument("userCursor") @Size(min = 24, message = "{validation.mongo-id.size.too-short}") @Size(max = 24, message = "{validation.mongo-id.size.too-long}") String userCursor, // nullable: true
            //    @Max(value = 100, message = "You can request data from up to {value} user{value > 1 ? 's' : ''}")
            @Argument("usersLimit") @Max(value = 50, message = "{validation.sudokus-limit}") @NotNull Integer usersLimit
    ) {
        return this.sudokusService.findUsersWhoLikeSudoku(parent.getId(), usersLimit, userCursor);
    }

// there is no point in implementing this as anyway each favoritedBy query requires a separate cursor as argument
//    @BatchMapping(typeName = "Sudoku", field = "favoritedBy")
//    public Map<SudokuModel, UserFeedModel> manyFavoritedBy(
//            List<SudokuModel> parents,
//            @Argument("usersLimit") int usersLimit,
//            @Argument("userCursor") String userCursor,
//            GraphQLContext graphQLContext,
//            DataFetchingEnvironment env
//    ) {}

// there is no need to define batch mapping for the list of sudokus because they are retrieved as feed in the SudokuFeed model
//    @BatchMapping (typeName = "SudokuFeed", field = "sudokus")
//    public Map<SudokuFeed, List<SudokuModel>> sudokus(List<SudokuFeed> sudokuFeeds) {
//        return
//    }
}

// DataFetchingEnvironment provides access to a map of field-specific argument values. The values can be simple scalar values (e.g. String, Long), a Map of values for more complex input, or a List of values. Use the @Argument annotation to have an argument bound to a target object and injected into the handler method. Binding is performed by mapping argument values to a primary data constructor of the expected method parameter type, or by using a default constructor to create the object and then map argument values to its properties. This is repeated recursively, using all nested argument values and creating nested target objects accordingly. If binding fails, a BindException is raised with binding issues accumulated as field errors where the field of each error is the argument path where the issue occurred. Prior to 1.2 you can use @Argument with a Map<String, Object> argument, to obtain the raw value of the argument. For example: @Argument Map<String, Object> mutationInput. Prior to 1.2, @Argument Map<String, Object> returned the full arguments map if the annotation did not specify a name. After 1.2, @Argument with Map<String, Object> always returns the raw argument value, matching either to the name specified in the annotation, or to the parameter name. For access to the full arguments map, use @Arguments instead. Use the @Arguments annotation, if you want to bind the full arguments map onto a single target Object, in contrast to @Argument, which binds a specific, named argument. For example, @Argument BookInput bookInput uses the value of the argument "bookInput" to initialize BookInput, while @Arguments uses the full arguments map and in that case, top-level arguments are bound to BookInput properties. You can use @Arguments with a Map<String, Object> argument, to obtain the raw map of all argument values

// @ArgumentValue is a simple container for the resulting value, along with a flag to indicate whether the input argument was omitted altogether. You can use this instead of @Argument, in which case the argument name is determined from the method parameter name, or together with @Argument to specify the argument name. ArgumentValue is also supported as a field within the object structure of an @Argument method parameter, either initialized via a constructor argument or via a setter, including as a field of an object nested at any level below the top level object.
//	@MutationMapping
//	public void addBook(ArgumentValue<BookInput> bookInput) {
//		if (!bookInput.isOmitted()) {
//			BookInput value = bookInput.value();
//		}
//	 }
//   @QueryMapping
//   public String test(Optional<Integer> intValue) {
//       return testService.getData(intValue);
//   }