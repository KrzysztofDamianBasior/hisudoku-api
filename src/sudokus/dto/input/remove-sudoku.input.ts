import { IsString } from 'class-validator';
import { InputType, Field, ID } from '@nestjs/graphql';

@InputType()
export class RemoveSudokuInput {
  @IsString()
  @Field(() => ID)
  sudokuId: string;
}
