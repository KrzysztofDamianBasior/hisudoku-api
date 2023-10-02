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
import { AccessTokenPayload } from 'src/auth/accessTokenPayload';
import { SudokuFeedArgs } from '../dto/args/sudoku-feed.args';

import { Logger } from '@nestjs/common';

@Resolver(() => MyAccount)
export class MyAccountResolver {
  constructor(
    private readonly usersService: UsersService,
    private readonly sudokusService: SudokusService,
  ) {}
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // QUERIES
  @UseGuards(GqlAuthGuard)
  @Query(() => MyAccount, {
    name: 'myAccount',
    description: `
    A query retrieving user account information available only to the owner
        
    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async findMe(
    @CurrentUser() user: AccessTokenPayload,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.findMyAccountById({ userId: user.sub });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // MUTATIONS
  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, {
    name: 'updateMyUsername',
    description: `
    Mutation available only to the account owner that modifies the username
            
    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async updateMyUsername(
    @CurrentUser() user: AccessTokenPayload,
    @Args('updateMyUsernameInput') updateMyUsernameInput: UpdateMyUsernameInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.updateMyUsername({
      userId: user.sub,
      newUsername: updateMyUsernameInput.newUsername,
    });
  }

  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, {
    name: 'updateMyEmail',
    description: `
    A mutation available only to the account owner that starts the process of updating the e-mail address

    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async updateMyEmail(
    @CurrentUser() user: AccessTokenPayload,
    @Args('updateMyEmailInput') updateMyEmailInput: UpdateMyEmailInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.updateMyEmail({
      userId: user.sub,
      newEmail: updateMyEmailInput.newEmail,
    });
  }

  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, {
    name: 'updateMyPassword',
    description: `
    A mutation available only to the account owner that updates the password

    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async updateMyPassword(
    @CurrentUser() user: AccessTokenPayload,
    @Args('updateMyPasswordInput') updateMyPasswordInput: UpdateMyPasswordInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.updateMyPassword({
      userId: user.sub,
      newPassword: updateMyPasswordInput.newPassword,
    });
  }

  @UseGuards(GqlAuthGuard)
  @Mutation(() => MyAccount, {
    name: 'removeMyAccount',
    description: `
    A mutation available only to the account owner that removes the account
    
    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async removeMyAccount(
    @CurrentUser() user: AccessTokenPayload,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.usersService.removeMyAccountById({ userId: user.sub });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // NESTED QUIRES

  @ResolveField('createdSudokus', () => SudokuFeed, {
    description: 'A list of sudokus created by the user',
  })
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
