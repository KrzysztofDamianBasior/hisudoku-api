import { IsNotEmpty, IsString } from 'class-validator';
import { InputType, Field, ID } from '@nestjs/graphql';

@InputType({
  description:
    'The parameters used by the sudokus resolver to remove one sudoku',
})
export class RemoveSudokuInput {
  @Field(() => ID, {
    description: 'ID of the sudoku that we request to be deleted',
  })
  @IsString()
  @IsNotEmpty()
  sudokuId: string;
}
