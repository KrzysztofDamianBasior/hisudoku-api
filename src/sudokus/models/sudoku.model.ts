import {
  Field,
  ObjectType,
  ID,
  GraphQLISODateTime,
  Int,
} from '@nestjs/graphql';
import { IsDate, IsNumber, IsString } from 'class-validator';
import { User } from 'src/users/models/user.model';
import { Types } from 'mongoose';
import { UserFeed } from 'src/users/models/userFeed.model';

@ObjectType({ description: 'The sudoku entity information' })
export class Sudoku {
  @IsString()
  @Field(() => ID, { description: 'The sudoku identifier' })
  id: string;

  @IsDate()
  @Field(() => GraphQLISODateTime, { description: 'The sudoku creation date' })
  createdAt: Date;

  @IsDate()
  @Field(() => GraphQLISODateTime, {
    description: 'A date of the last update of the sudoku content',
  })
  updatedAt: Date;

  @Field(() => User, { description: 'Author of the sudoku' })
  author: Types.ObjectId | string;

  @IsString()
  @Field(() => String, { description: 'The sudoku content' })
  content: string;

  @IsNumber()
  @Field(() => Int, { description: 'Number of likes accumulated by sudoku' })
  favoriteCount: number;

  @Field(() => UserFeed, {
    description: 'Users who liked this sudoku',
  })
  favoritedBy: (Types.ObjectId | string)[];
}
