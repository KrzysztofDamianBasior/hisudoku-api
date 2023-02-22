import { Field, ObjectType } from '@nestjs/graphql';
import { Sudoku } from './sudoku.model';

@ObjectType()
export class SudokuFeed {
  @Field(() => [Sudoku], { description: '' })
  sudokus: Sudoku[];

  @Field(() => Boolean, { description: '' })
  hasNextPage: boolean;

  @Field(() => String, { description: '' })
  cursor: string;
}
