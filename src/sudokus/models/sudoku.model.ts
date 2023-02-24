import {
  Field,
  ObjectType,
  ID,
  GraphQLISODateTime,
  Int,
} from '@nestjs/graphql';
import { IsDate, IsNumber, IsString } from 'class-validator';
import { User } from 'src/users/models/user.model';

@ObjectType()
export class Sudoku {
  @IsString()
  @Field(() => ID, { description: 'Sudoku identifier' })
  id: string;

  @IsDate()
  @Field(() => GraphQLISODateTime, { description: '' })
  createdAt: Date;

  @IsDate()
  @Field(() => GraphQLISODateTime, { description: '' })
  updatedAt: Date;

  @Field(() => User, { description: '' })
  author: User;

  @IsString()
  @Field(() => String, { description: '' })
  content: string;

  @IsNumber()
  @Field(() => Int, { description: '' })
  favoriteCount: number;

  @Field(() => [User])
  favoritedBy: User[];
}
