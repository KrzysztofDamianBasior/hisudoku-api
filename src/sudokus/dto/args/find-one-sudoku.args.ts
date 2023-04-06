import { ArgsType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@ArgsType()
export class FindOneSudokuArgs {
  @Field(() => ID, { description: '' })
  @IsNotEmpty()
  @IsString()
  sudokuId: string;
}
