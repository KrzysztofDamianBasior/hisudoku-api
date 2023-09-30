import { IsNotEmpty, IsString } from 'class-validator';
import { InputType, Field, ID } from '@nestjs/graphql';

@InputType({
  description:
    'The parameters used by the sudokus resolver to update the sudoku content',
})
export class UpdateSudokuInput {
  @Field(() => ID, {
    description: 'ID of the sudoku whose content will be updated',
  })
  @IsString()
  @IsNotEmpty()
  sudokuId: string;

  @Field(() => String, { description: 'A new content' })
  @IsString()
  @IsNotEmpty()
  sudokuContent: string;
}
