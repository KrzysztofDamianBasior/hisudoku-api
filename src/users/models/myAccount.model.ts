import { Field, ObjectType, ID, GraphQLISODateTime } from '@nestjs/graphql';
import { SudokuFeed } from 'src/sudokus/models/sudokuFeed.model';

@ObjectType({
  description: 'The information available only to the account owner',
})
export class MyAccount {
  @Field(() => ID, { description: 'The account owner identifier' })
  id: string;

  @Field(() => SudokuFeed, {
    description: 'The sudokus created by the owner of the account',
  })
  createdSudokus: string[];

  @Field(() => GraphQLISODateTime, {
    description: 'The account creation date',
  })
  createdAt?: Date;

  @Field(() => GraphQLISODateTime, {
    description: 'A date of the last update of the account',
  })
  updatedAt?: Date;

  @Field(() => String, { description: 'The username' })
  username: string;

  @Field(() => String, {
    description: "The account owner's e-mail address",
    nullable: true,
  })
  email: string | null;

  @Field(() => [String], { description: 'The roles assigned to the user' })
  roles: string[];
}
