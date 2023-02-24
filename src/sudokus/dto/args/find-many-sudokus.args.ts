import { ArgsType, Field, Int, ID } from '@nestjs/graphql';
import { IsNumber, IsString } from 'class-validator';

@ArgsType()
export class FindManySudokusArgs {
  @IsString()
  @Field(() => ID)
  sudokuCursor: string;

  @IsNumber()
  @Field(() => Int)
  sudokusLimit: number;

  @IsNumber()
  @Field(() => Int)
  favoritedByLimit: number;
}
