import { IsString } from 'class-validator';
import { InputType, Field, ID } from '@nestjs/graphql';

@InputType()
export class RemoveSudokuInput {
  @Field(() => ID, { description: '' })
  @IsString()
  sudokuId: string;
}
