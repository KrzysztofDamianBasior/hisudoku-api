import { Field, ObjectType } from '@nestjs/graphql';
import { IsBoolean, IsString } from 'class-validator';

import { Sudoku } from './sudoku.model';

@ObjectType()
export class SudokuFeed {
  @Field(() => [Sudoku], { description: '' })
  sudokus: Sudoku[];

  @IsBoolean()
  @Field(() => Boolean, { description: '' })
  hasNextPage: boolean;

  @IsString()
  @Field(() => String, { description: '' })
  cursor: string;
}
