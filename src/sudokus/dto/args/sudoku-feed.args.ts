import { ArgsType, Field, Int, ID } from '@nestjs/graphql';
import { IsNumber, IsString } from 'class-validator';

@ArgsType()
export class SudokuFeedArgs {
  @Field(() => ID, { nullable: true, description: '' })
  @IsString()
  sudokuCursor: string;

  @Field(() => Int, { description: '' })
  @IsNumber()
  sudokusLimit: number;
}
