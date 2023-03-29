import { ArgsType, Field, ID } from '@nestjs/graphql';
import { IsString } from 'class-validator';

@ArgsType()
export class FindOneSudokuArgs {
  @Field(() => ID, { description: '' })
  @IsString()
  sudokuId: string;
}
