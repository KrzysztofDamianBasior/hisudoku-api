import { Field, ObjectType } from '@nestjs/graphql';
import { IsBoolean, IsString } from 'class-validator';

import { Sudoku } from './sudoku.model';

@ObjectType({ description: 'A sudoku feed structure' })
export class SudokuFeed {
  @Field(() => [Sudoku], { description: 'Sudokus list' })
  sudokus: Sudoku[];

  @IsBoolean()
  @Field(() => Boolean, {
    description: 'An indicator of whether there is more data to retrieve',
  })
  hasNextPage: boolean;

  @IsString()
  @Field(() => String, {
    description:
      'A pointer used to keep track of where in the data set the next items should be fetched from',
    nullable: true,
  })
  cursor: string;
}
