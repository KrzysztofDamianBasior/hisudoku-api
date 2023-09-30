import { InputType, Field } from '@nestjs/graphql';
import { IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the sudokus resolver to create the sudoku',
})
export class CreateSudokuInput {
  @Field(() => String, {
    description: 'content of the sudoku that user request to create',
  })
  @IsString()
  content: string;
}
