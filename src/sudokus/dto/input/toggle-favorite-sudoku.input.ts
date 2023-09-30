import { InputType, Field, ID } from '@nestjs/graphql';
import { IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the sudokus resolver to toggle the sudoku like',
})
export class ToggleFavoriteSudokuInput {
  @Field(() => ID, {
    description: 'ID of the sudoku that the user toggle like',
  })
  @IsString()
  sudokuId: string;
}
