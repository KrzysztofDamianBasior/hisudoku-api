import { InputType, Field, ID } from '@nestjs/graphql';
import { IsString } from 'class-validator';

@InputType()
export class ToggleFavoriteSudokuInput {
  @Field(() => ID, { description: '' })
  @IsString()
  sudokuId: string;
}
