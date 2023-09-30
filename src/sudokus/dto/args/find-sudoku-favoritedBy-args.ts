import { ArgsType, Field, Int, ID } from '@nestjs/graphql';
import { IsNumber, IsString } from 'class-validator';

@ArgsType()
export class FindSudokuFavoritedByArgs {
  @Field(() => ID, {
    nullable: true,
    description:
      'A pointer used to keep track of where in the data set the next items should be fetched from',
  })
  @IsString()
  userCursor: string;

  @Field(() => Int, {
    description: 'The maximum number of items returned by the query',
  })
  @IsNumber()
  usersLimit: number;
}
