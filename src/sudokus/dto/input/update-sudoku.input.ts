import { IsNumber, IsString } from 'class-validator';
import { InputType, Field, Int, PartialType, ID } from '@nestjs/graphql';

@InputType()
export class UpdateSudokuInput {
  @IsString()
  @Field(() => ID)
  sudokuId: string;

  @IsString()
  @Field(() => String)
  sudokuContent: string;

  @IsString()
  @Field(() => ID)
  favoritedByCursor: string;

  @IsNumber()
  @Field(() => Int)
  favoritedByLimit: number;
}
