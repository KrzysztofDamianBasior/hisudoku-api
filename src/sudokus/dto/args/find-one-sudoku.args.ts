import { ArgsType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@ArgsType()
export class FindOneSudokuArgs {
  @Field(() => ID, { description: 'The ID of the searched sudoku' })
  @IsNotEmpty()
  @IsString()
  sudokuId: string;
}
