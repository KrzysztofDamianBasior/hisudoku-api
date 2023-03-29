import { ArgsType, Field, ID, Int } from '@nestjs/graphql';
import { IsNotEmpty, IsOptional } from 'class-validator';

@ArgsType()
export class UserFeedArgs {
  @Field(() => ID, { nullable: true, description: '' })
  userCursor: string | null;

  @Field(() => Int, { description: '' })
  @IsOptional()
  @IsNotEmpty()
  usersLimit: number;
}
