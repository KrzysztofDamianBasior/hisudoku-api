import { IsString } from 'class-validator';
import { InputType, Field, ID } from '@nestjs/graphql';

@InputType()
export class UpdateSudokuInput {
  @Field(() => ID)
  @IsString()
  sudokuId: string;

  @Field(() => String)
  @IsString()
  sudokuContent: string;
}
