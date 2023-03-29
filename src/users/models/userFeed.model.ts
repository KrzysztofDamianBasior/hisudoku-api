import { Field, ID, ObjectType } from '@nestjs/graphql';
import { IsBoolean, IsString } from 'class-validator';
import { User } from './user.model';

@ObjectType()
export class UserFeed {
  @Field(() => [User], { description: '' })
  users: User[];

  @Field(() => Boolean, { description: '' })
  @IsBoolean()
  hasNextPage: boolean;

  @Field(() => ID, { description: '' })
  @IsString()
  cursor: string;
}
