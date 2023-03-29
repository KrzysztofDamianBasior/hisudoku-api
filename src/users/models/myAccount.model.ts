import { Field, ObjectType, ID, GraphQLISODateTime } from '@nestjs/graphql';
import { SudokuFeed } from 'src/sudokus/models/sudokuFeed.model';

@ObjectType()
export class MyAccount {
  @Field(() => ID, { description: 'User identifier' })
  id: string;

  @Field(() => SudokuFeed, { description: '' })
  createdSudokus: string[];

  @Field(() => GraphQLISODateTime, { description: '' })
  createdAt?: Date;

  @Field(() => GraphQLISODateTime, { description: '' })
  updatedAt?: Date;

  @Field(() => String, { description: '' })
  username: string;

  @Field(() => String, { description: '', nullable: true })
  email: string;

  @Field(() => [String], { description: '' })
  roles: string[];
}
