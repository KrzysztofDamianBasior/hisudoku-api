import {
  Resolver,
  Query,
  Mutation,
  Args,
  ResolveField,
  Parent,
} from '@nestjs/graphql';
import { SudokusService } from '../services/sudokus.service';
import { CreateSudokuInput } from '../dto/input/create-sudoku.input';
import { UpdateSudokuInput } from '../dto/input/update-sudoku.input';
import { Roles } from 'src/auth/roles';
import { Sudoku } from '../models/sudoku.model';
import { SudokuFeed } from '../models/sudokuFeed.model';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { JwtAuthGuard } from 'src/auth/guards/jwt-auth.guard';
import { GqlAuthGuard } from 'src/auth/guards/gql-auth.guard';
import { UseGuards } from '@nestjs/common';
import { CurrentUser } from 'src/auth/current-user.decorator';
import { FindManySudokusArgs } from '../dto/args/find-many-sudokus.args';
import { FindOneSudokuArgs } from '../dto/args/find-one-sudoku.args';
import { ToggleFavoriteSudokuInput } from '../dto/input/toggle-favorite-sudoku.input';
import { User } from 'src/users/models/user.model';
import { RemoveSudokuInput } from '../dto/input/remove-sudoku.input';

@Roles('User')
@Resolver(() => Sudoku)
export class SudokusResolver {
  constructor(private readonly sudokusService: SudokusService) {}

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Queries

  @UseGuards(GqlAuthGuard)
  @UseGuards(JwtAuthGuard)
  @UseGuards(RolesGuard)
  @Query(() => [SudokuFeed], { name: 'sudokus' })
  async findManySudokus(
    @CurrentUser() user: User,
    @Args() findManySudokusArgs: FindManySudokusArgs,
  ): Promise<SudokuFeed> {
    return this.sudokusService.findManySudokus(findManySudokusArgs);
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(JwtAuthGuard)
  @UseGuards(RolesGuard)
  @Query(() => Sudoku, { name: 'sudokus' })
  async findOne(
    @CurrentUser() user: User,
    @Args() findOneSudokuArgs: FindOneSudokuArgs,
  ): Promise<Sudoku> {
    return this.sudokusService.findOneSudoku(findOneSudokuArgs);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  // Mutations

  @UseGuards(GqlAuthGuard)
  @UseGuards(JwtAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku)
  async createSudoku(
    @CurrentUser() user: User,
    @Args('createSudokusInput') createSudokuInput: CreateSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.createSudoku({
      authorId: user.id,
      createSudokuInput,
    });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(JwtAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku)
  async updateSudoku(
    @CurrentUser() user: User,
    @Args('updateSudokuInput') updateSudokuInput: UpdateSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.updateSudokuContent({
      userId: user.id,
      updateSudokuInput,
    });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(JwtAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku)
  async removeSudoku(
    @CurrentUser() user: User,
    @Args('removeSudokuInput') removeSudokuInput: RemoveSudokuInput,
  ): Promise<Sudoku> {
    return this.sudokusService.remove({ userId: user.id, removeSudokuInput });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(JwtAuthGuard)
  @UseGuards(RolesGuard)
  @Mutation(() => Sudoku)
  async toggleFavorite(
    @CurrentUser() user: User,
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
  @ResolveField()
  async author(@Parent() sudoku: Sudoku): Promise<User> {
    return this.sudokusService.findSudokuAuthor(sudoku.id);
  }

  @ResolveField()
  async favoritedBy(@Parent() sudoku: Sudoku): Promise<User[]> {
    return this.sudokusService.findUsersWhoLikeSudoku({
      sudokuId: sudoku.id,
      favoritedByCursor: null,
      favoritedByLimit: 50,
    });
  }
}
