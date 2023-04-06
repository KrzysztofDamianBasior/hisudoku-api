import { UseGuards } from '@nestjs/common';
import { Resolver, Query, Mutation, Args } from '@nestjs/graphql';

import { Roles } from 'src/auth/roles';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { GqlAuthGuard } from 'src/auth/guards/gql-auth.guard';

import { FindOneUserArgs } from '../dto/args/find-one-user.args';
import { UserFeedArgs } from '../dto/args/user-feed.args';
import { GrantAdminPermissionsInput } from '../dto/input/grant-admin-permissions.input';
import { UpdateOneUsernameInput } from '../dto/input/update-one-username.input';
import { RemoveOneInput } from '../dto/input/remove-one.input';

import { User as GQLUser } from '../models/user.model';
import { UserFeed } from '../models/userFeed.model';

import { UsersService } from '../services/users.service';

@Roles('User')
@Resolver(() => GQLUser)
export class UsersResolver {
  constructor(private readonly usersService: UsersService) {}
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // QUERIES
  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Query(() => GQLUser, {
    name: 'user',
    description: `
    A query retrieving information about one of the users
    
    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async findOneUser(
    @Args() findOneUserArgs: FindOneUserArgs,
  ): Promise<GQLUser> {
    return this.usersService.findOneById({ userId: findOneUserArgs.userId });
  }

  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Query(() => UserFeed, {
    name: 'userFeed',
    description: `
    A query fetching an user feed
 
    only for logged in, required roles: [User]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async userFeed(@Args() userFeedArgs: UserFeedArgs): Promise<UserFeed> {
    console.log(userFeedArgs);
    return this.usersService.userFeed({ ...userFeedArgs });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // MUTATIONS
  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Roles('Admin')
  @Mutation(() => GQLUser, {
    name: 'updateOneUserUsername',
    description: `
    A mutation that changes the username of the selected user
    
    only for logged in, required roles: [Admin]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async updateOneUsername(
    @Args('updateOneUserUsername') updateOneUsername: UpdateOneUsernameInput,
  ): Promise<GQLUser> {
    return this.usersService.updateOneUsername({
      userId: updateOneUsername.userId,
      newUsername: updateOneUsername.newUsername,
    });
  }

  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Roles('Admin')
  @Mutation(() => GQLUser, {
    name: 'removeOneUser',
    description: `
    A mutation that removes one of the users
    
    only for logged in, required roles: [Admin]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async removeOne(
    @Args('removeOneUserInput') removeOneUser: RemoveOneInput,
  ): Promise<GQLUser> {
    return this.usersService.removeOne({ userId: removeOneUser.userId });
  }

  @UseGuards(RolesGuard)
  @UseGuards(GqlAuthGuard)
  @Roles('Admin')
  @Mutation(() => GQLUser, {
    name: 'grantAdminPermissions',
    description: `
    A mutation that grants an user administrator privileges
    
    only for logged in, required roles: [Admin]
    Bearer authentication

    HTTP Headers:
    {
      "Authorization": "Bearer your-JWT"
    }
    `,
  })
  async grantAdminPermissions(
    @Args('grantAdminPermissionsInput')
    grantAdminPermissionsInput: GrantAdminPermissionsInput,
  ): Promise<GQLUser> {
    return this.usersService.grantAdminPermissions({
      userId: grantAdminPermissionsInput.userId,
    });
  }
}
