import { ArgsType, Field, ID, Int } from '@nestjs/graphql';
import { IsNumber, IsString } from 'class-validator';

@ArgsType()
export class FindOneSudokuArgs {
  @IsString()
  @Field(() => ID)
  sudokuId: string;

  @IsString()
  @Field(() => ID)
  favoritedByCursor: string;

  @IsNumber()
  @Field(() => Int)
  favoritedByLimit: number;
}
