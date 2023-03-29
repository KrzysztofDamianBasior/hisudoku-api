import { ArgsType, Field, Int, ID } from '@nestjs/graphql';
import { IsNumber, IsString } from 'class-validator';

@ArgsType()
export class FindSudokuFavoritedByArgs {
  @Field(() => ID, { nullable: true, description: '' })
  @IsString()
  userCursor: string;

  @Field(() => Int, { description: '' })
  @IsNumber()
  usersLimit: number;
}
