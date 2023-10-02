import {
  Resolver,
  Query,
  Mutation,
  Args,
  ResolveField,
  Parent,
} from '@nestjs/graphql';
import { UseGuards } from '@nestjs/common';

import { SudokusService } from '../services/sudokus.service';

import { Roles } from 'src/auth/roles';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { GqlAuthGuard } from 'src/auth/guards/gql-auth.guard';
import { CurrentUser } from 'src/auth/current-user.decorator';

import { CreateSudokuInput } from '../dto/input/create-sudoku.input';
import { UpdateSudokuInput } from '../dto/input/update-sudoku.input';
import { SudokuFeedArgs } from '../dto/args/sudoku-feed.args';
import { FindOneSudokuArgs } from '../dto/args/find-one-sudoku.args';
import { ToggleFavoriteSudokuInput } from '../dto/input/toggle-favorite-sudoku.input';
import { RemoveSudokuInput } from '../dto/input/remove-sudoku.input';
import { FindSudokuFavoritedByArgs } from '../dto/args/find-sudoku-favoritedBy-args';

import { Sudoku } from '../models/sudoku.model';
import { SudokuFeed } from '../models/sudokuFeed.model';
import { User as GQLUser } from 'src/users/models/user.model';
import { UserFeed } from 'src/users/models/userFeed.model';
import { AccessTokenPayload } from 'src/auth/accessTokenPayload';

@Roles('User')
@Resolver(() => Sudoku)
export class SudokusResolver {
  constructor(private readonly sudokusService: SudokusService) {}

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Queries
  @Query(() => SudokuFeed, {
    name: 'sudokuFeed',
    description: `
    A query fetching a sudoku feed

    only for logged in, no required roles
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async sudokuFeed(
    @Args() sudokuFeedArgs: SudokuFeedArgs,
  ): Promise<SudokuFeed> {
    return this.sudokusService.sudokuFeed(sudokuFeedArgs);
  }

  @Query(() => Sudoku, {
    name: 'sudoku',
    description: `
    A query retrieving information about one of the sudokus
    
    only for logged in, no required roles
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async findOne(@Args() findOneSudokuArgs: FindOneSudokuArgs): Promise<Sudoku> {
    return this.sudokusService.findOneSudoku(findOneSudokuArgs);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Mutations
  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Mutation(() => Sudoku, {
    name: 'createSudoku',
    description: `
    A mutation that create sudoku

    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
  `,
  })
  async createSudoku(
    @CurrentUser() user: AccessTokenPayload,
    @Args('createSudokusInput') createSudokuInput: CreateSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.createSudoku({
      authorId: user.sub,
      createSudokuInput,
    });
  }

  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Mutation(() => Sudoku, {
    name: 'updateSudoku',
    description: `
      A mutation that update sudoku if the user is the author of the sudoku

      only for logged in, required roles: [User]
      Bearer authentication

      HTTP Headers:
      {
        "Authorization": "Bearer your-JWT"
      }
    `,
  })
  async updateSudoku(
    @CurrentUser() user: AccessTokenPayload,
    @Args('updateSudokuInput') updateSudokuInput: UpdateSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.updateSudokuContent({
      userId: user.sub,
      updateSudokuInput,
    });
  }

  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Mutation(() => String, {
    name: 'removeSudoku',
    description: `
    A mutation that remove sudoku if the user is the author of the sudoku

    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
  `,
  })
  async removeSudoku(
    @CurrentUser() user: AccessTokenPayload,
    @Args('removeSudokuInput') removeSudokuInput: RemoveSudokuInput,
  ): Promise<string> {
    return this.sudokusService.remove({ userId: user.sub, removeSudokuInput });
  }

  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Mutation(() => Sudoku, {
    name: 'toggleFavoriteSudoku',
    description: `
    A mutation that toggle sudoku like
    
    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
  `,
  })
  async toggleFavorite(
    @CurrentUser() user: AccessTokenPayload,
    @Args('toggleFavoriteSudokuInput')
    toggleFavoriteSudokuInput: ToggleFavoriteSudokuInput,
  ) {
    return this.sudokusService.toggleFavorite({
      userId: user.sub,
      toggleFavoriteSudokuInput,
    });
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Nested Queries
  @ResolveField('author', () => GQLUser, {
    description: 'Sudoku author',
  })
  async author(@Parent() sudoku: Sudoku): Promise<GQLUser> {
    return this.sudokusService.findSudokuAuthor({ sudokuId: sudoku.id });
  }

  @ResolveField('favoritedBy', () => UserFeed, {
    description: 'A list of users who liked the sudoku',
  })
  async favoritedBy(
    @Parent() sudoku: Sudoku,
    @Args() findSudokuFavoritedBy: FindSudokuFavoritedByArgs,
  ): Promise<UserFeed> {
    return this.sudokusService.findUsersWhoLikeSudoku({
      sudokuId: sudoku.id,
      favoritedByCursor: findSudokuFavoritedBy.userCursor,
      favoritedByLimit: findSudokuFavoritedBy.usersLimit,
    });
  }
}
