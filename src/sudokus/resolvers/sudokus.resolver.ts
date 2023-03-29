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

@Roles('User')
@Resolver(() => Sudoku)
export class SudokusResolver {
  constructor(private readonly sudokusService: SudokusService) {}

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Queries
  @Query(() => SudokuFeed, { name: 'sudokuFeed' })
  async sudokuFeed(
    @Args() sudokuFeedArgs: SudokuFeedArgs,
  ): Promise<SudokuFeed> {
    return this.sudokusService.sudokuFeed(sudokuFeedArgs);
  }

  @Query(() => Sudoku, { name: 'sudoku' })
  async findOne(@Args() findOneSudokuArgs: FindOneSudokuArgs): Promise<Sudoku> {
    return this.sudokusService.findOneSudoku(findOneSudokuArgs);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Mutations
  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku, { name: 'createSudoku' })
  async createSudoku(
    @CurrentUser() user: GQLUser,
    @Args('createSudokusInput') createSudokuInput: CreateSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.createSudoku({
      authorId: user.id,
      createSudokuInput,
    });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku, { name: 'updateSudoku' })
  async updateSudoku(
    @CurrentUser() user: GQLUser,
    @Args('updateSudokuInput') updateSudokuInput: UpdateSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.updateSudokuContent({
      userId: user.id,
      updateSudokuInput,
    });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku, { name: 'removeSudoku' })
  async removeSudoku(
    @CurrentUser() user: GQLUser,
    @Args('removeSudokuInput') removeSudokuInput: RemoveSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.remove({ userId: user.id, removeSudokuInput });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku, { name: 'toggleFavoriteSudoku' })
  async toggleFavorite(
    @CurrentUser() user: GQLUser,
    @Args('toggleFavoriteSudokuInput')
    toggleFavoriteSudokuInput: ToggleFavoriteSudokuInput,
  ) {
    return this.sudokusService.toggleFavorite({
      userId: user.id,
      toggleFavoriteSudokuInput,
    });
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Nested Queries
  @ResolveField('author', () => GQLUser)
  async author(@Parent() sudoku: Sudoku): Promise<GQLUser> {
    return this.sudokusService.findSudokuAuthor({ sudokuId: sudoku.id });
  }

  @ResolveField('favoritedBy', () => UserFeed)
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
