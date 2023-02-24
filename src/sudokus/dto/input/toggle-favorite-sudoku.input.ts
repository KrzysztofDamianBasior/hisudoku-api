import { InputType, Int, Field, ID } from '@nestjs/graphql';
import { IsNumber, IsString } from 'class-validator';

@InputType()
export class ToggleFavoriteSudokuInput {
  @IsString()
  @Field(() => ID, { description: '' })
  sudokuId: string;

  @IsString()
  @Field(() => ID, { description: '' })
  favoritedByCursor: string;

  @IsNumber()
  @Field(() => Int, { description: '' })
  favoritedByLimit: number;
}
