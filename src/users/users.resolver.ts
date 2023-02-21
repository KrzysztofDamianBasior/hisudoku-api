import { Resolver, Query, Mutation, Args, Int } from '@nestjs/graphql';
import { UsersService } from './services/users.service';
import { User } from './db/user.schema';
import { CreateUserInput } from './dto/input/create-user.input';
import { UpdateUserInput } from './dto/input/update-user.input';

@Resolver(() => User)
export class UsersResolver {
  constructor(private readonly usersService: UsersService) {}

  @Mutation(() => User)
  createUser(@Args('createUserInput') createUserInput: CreateUserInput) {
    return this.usersService.create(createUserInput);
  }

  @Query(() => [User], { name: 'users', nullable: 'items' })
  findAll() {
    return this.usersService.findAll();
  }

  @Query(() => User, { name: 'user', nullable: true })
  findOne(@Args('id', { type: () => Int }) id: number) {
    return this.usersService.findOne(id);
  }

  //@UseGuards(GqlAuthGuard)
  //getUser(@CurrentUser() user: Userr,  @Args() getUserArgs: GetUserArgs): User
  @Mutation(() => User)
  updateUser(
    @Args('updateUserInput') updateUserInput: UpdateUserInput, //: Promise<User>
  ) {
    return this.usersService.update(updateUserInput.id, updateUserInput);
  }

  @Mutation(() => User)
  removeUser(@Args('id', { type: () => Int }) id: number) {
    return this.usersService.remove(id);
  }
}
