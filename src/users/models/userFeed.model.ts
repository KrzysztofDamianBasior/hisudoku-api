import { Field, ID, ObjectType } from '@nestjs/graphql';
import { User } from './user.model';

@ObjectType({ description: 'An user feed structure' })
export class UserFeed {
  @Field(() => [User], { description: 'An user list' })
  users: User[];

  @Field(() => Boolean, {
    description: 'An indicator of whether there is more data to retrieve',
  })
  hasNextPage: boolean;

  @Field(() => ID, {
    description:
      'A constant pointer used to keep track of where in the data set the next items should be fetched from',
    nullable: true,
  })
  cursor: string;
}
