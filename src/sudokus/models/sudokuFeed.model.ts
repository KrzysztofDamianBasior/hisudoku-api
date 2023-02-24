import { Field, ObjectType } from '@nestjs/graphql';
import { Sudoku } from './sudoku.model';
import { IsBoolean, IsString } from 'class-validator';

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
