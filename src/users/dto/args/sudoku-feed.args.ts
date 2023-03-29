import { ArgsType, Field, ID, Int } from '@nestjs/graphql';
import { IsNotEmpty, IsOptional } from 'class-validator';

@ArgsType()
export class SudokuFeedArgs {
  @Field(() => ID, { nullable: true, description: '' })
  sudokuCursor: string | null;

  @Field(() => Int, { description: '' })
  @IsOptional()
  @IsNotEmpty()
  sudokusLimit: number;
}
