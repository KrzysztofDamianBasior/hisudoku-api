import { UseGuards } from '@nestjs/common';
import { Resolver, Query, Mutation, Args } from '@nestjs/graphql';

import { Roles } from 'src/auth/roles';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { GqlAuthGuard } from 'src/auth/guards/gql-auth.guard';

import { FindOneUserArgs } from '../dto/args/find-one-user.args';
import { GrantAdminPermissionsInput } from '../dto/input/grant-admin-permissions.input';
import { UpdateOneUsernameInput } from '../dto/input/update-one-username.input';

import { User as GQLUser } from '../models/user.model';
import { UserFeed } from '../models/userFeed.model';

import { UsersService } from '../services/users.service';
import { UserFeedArgs } from '../dto/args/user-feed.args';
import { RemoveOneInput } from '../dto/input/remove-one.input';

@Roles('User')
@Resolver(() => GQLUser)
export class UsersResolver {
  constructor(private readonly usersService: UsersService) {}
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // QUERIES
  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Query(() => GQLUser, { name: 'user', description: '' })
  async findOneUser(
    @Args() findOneUserArgs: FindOneUserArgs,
  ): Promise<GQLUser> {
    return this.usersService.findOneById({ userId: findOneUserArgs.userId });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Query(() => [UserFeed], { name: 'userFeed', description: '' })
  async userFeed(@Args() UserFeed: UserFeedArgs): Promise<UserFeed> {
    return this.usersService.userFeed({ ...UserFeed });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // MUTATIONS
  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Roles('Admin')
  @Mutation(() => GQLUser, { name: 'updateOneUserUsername', description: '' })
  async updateOneUsername(
    @Args('updateOneUserUsername') updateOneUsername: UpdateOneUsernameInput,
  ): Promise<GQLUser> {
    return this.usersService.updateOneUsername({
      userId: updateOneUsername.userId,
      newUsername: updateOneUsername.newUsername,
    });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Roles('Admin')
  @Mutation(() => GQLUser, { name: 'removeOneUser', description: '' })
  async removeOne(
    @Args('removeOneUserInput') removeOneUser: RemoveOneInput,
  ): Promise<GQLUser> {
    return this.usersService.removeOne({ userId: removeOneUser.userId });
  }

  @UseGuards(GqlAuthGuard)
  @UseGuards(RolesGuard)
  @Roles('Admin')
  @Mutation(() => GQLUser, { name: 'grantAdminPermissions', description: '' })
  async grantAdminPermissions(
    @Args('grantAdminPermissionsInput')
    grantAdminPermissionsInput: GrantAdminPermissionsInput,
  ): Promise<GQLUser> {
    return this.usersService.grantAdminPermissions({
      userId: grantAdminPermissionsInput.userId,
    });
  }
}
