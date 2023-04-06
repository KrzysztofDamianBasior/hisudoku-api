import { ArgsType, Field, ID, Int } from '@nestjs/graphql';
import { IsNotEmpty, IsNumber, IsOptional, IsString } from 'class-validator';

@ArgsType()
export class SudokuFeedArgs {
  @Field(() => ID, {
    nullable: true,
    description:
      'A constant pointer used to keep track of where in the data set the next items should be fetched from',
  })
  @IsString()
  @IsOptional()
  sudokuCursor: string | null | undefined;

  @Field(() => Int, {
    description: 'The maximum number of items returned by the query',
  })
  @IsNumber()
  @IsNotEmpty()
  sudokusLimit: number;
}
