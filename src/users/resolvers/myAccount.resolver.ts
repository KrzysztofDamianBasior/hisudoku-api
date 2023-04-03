import {
  Resolver,
  Query,
  Mutation,
  Args,
  ResolveField,
  Parent,
  Info,
} from '@nestjs/graphql';
import { UseGuards } from '@nestjs/common';

import { UsersService } from '../services/users.service';
import { SudokusService } from 'src/sudokus/services/sudokus.service';

import { SudokuFeed } from 'src/sudokus/models/sudokuFeed.model';
import { MyAccount } from '../models/myAccount.model';

import { UpdateMyUsernameInput } from '../dto/input/update-my-username.input';
import { UpdateMyEmailInput } from '../dto/input/update-my-email.input';
import { UpdateMyPasswordInput } from '../dto/input/update-my-password.input';

import { CurrentUser } from 'src/auth/current-user.decorator';
import { GqlAuthGuard } from 'src/auth/guards/gql-auth.guard';
import { jwtPayload } from 'src/auth/jwtPayload';
import { ActivateEmailInput } from '../../auth/dto/activate-email.input';
import { SudokuFeedArgs } from '../dto/args/sudoku-feed.args';

@Resolver(() => MyAccount)
export class MyAccountResolver {
  constructor(
    private readonly usersService: UsersService,
    private readonly sudokusService: SudokusService,
  ) {}
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // QUERIES
  @UseGuards(GqlAuthGuard)
  @Query(() => MyAccount, { name: 'myAccount', description: '' })
  async findMe(
    @CurrentUser() user: jwtPayload,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.findMyAccountById({ userId: user.sub });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // MUTATIONS
  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, { name: 'updateMyUsername', description: '' })
  async updateMyUsername(
    @CurrentUser() user: jwtPayload,
    @Args('updateMyUsernameInput') updateMyUsernameInput: UpdateMyUsernameInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.updateMyUsername({
      userId: user.sub,
      newUsername: updateMyUsernameInput.newUsername,
    });
  }

  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, { name: 'updateMyEmail', description: '' })
  async updateMyEmail(
    @CurrentUser() user: jwtPayload,
    @Args('updateMyEmailInput') updateMyEmailInput: UpdateMyEmailInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.updateMyEmail({
      userId: user.sub,
      newEmail: updateMyEmailInput.email,
    });
  }

  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, { name: 'updateMyPassword', description: '' })
  async updateMyPassword(
    @CurrentUser() user: jwtPayload,
    @Args('updateMyPasswordInput') updateMyPasswordInput: UpdateMyPasswordInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.updateMyPassword({
      userId: user.sub,
      password: updateMyPasswordInput.password,
    });
  }

  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, { name: 'removeMyAccount', description: '' })
  async removeMyAccount(
    @CurrentUser() user: jwtPayload,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.removeMyAccountById({ userId: user.sub });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // NESTED QUIRES

  @ResolveField('createdSudokus', () => SudokuFeed, {})
  async createdSudokus(
    @Parent() user: MyAccount,
    @Args() sudokuFeedArgs: SudokuFeedArgs,
    @Info() info,
  ): Promise<SudokuFeed> {
    return this.sudokusService.findManySudokusByAuthor({
      author: user.id,
      sudokuCursor: sudokuFeedArgs.sudokuCursor,
      sudokusLimit: sudokuFeedArgs.sudokusLimit,
    });
  }
}
