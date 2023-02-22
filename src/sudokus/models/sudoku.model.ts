import {
  Field,
  ObjectType,
  ID,
  GraphQLISODateTime,
  Int,
} from '@nestjs/graphql';
import { User } from 'src/users/models/user.model';

@ObjectType()
export class Sudoku {
  @Field(() => ID, { description: 'Sudoku identifier' })
  id: string;

  @Field(() => GraphQLISODateTime, { description: '' })
  createdAt: Date;

  @Field(() => GraphQLISODateTime, { description: '' })
  updatedAt: Date;

  @Field(() => User, { description: '' })
  author: User;

  @Field(() => String, { description: '' })
  content: string;

  @Field(() => Int, { description: '' })
  favoriteCount: number;

  @Field(() => [User])
  favoritedBy: User[];
}
