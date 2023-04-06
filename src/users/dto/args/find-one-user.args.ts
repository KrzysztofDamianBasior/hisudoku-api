import { ArgsType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@ArgsType()
export class FindOneUserArgs {
  @Field(() => ID, { description: 'The account ID of the searched user' })
  @IsNotEmpty()
  @IsString()
  userId: string;
}
