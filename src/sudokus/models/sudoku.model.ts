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
  author: Types.ObjectId | string;

  @IsString()
  @Field(() => String, { description: '' })
  content: string;

  @IsNumber()
  @Field(() => Int, { description: '' })
  favoriteCount: number;

  @Field(() => UserFeed)
  favoritedBy: (Types.ObjectId | string)[];
}
