import { ArgsType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@ArgsType()
export class FindOneUserArgs {
  @Field(() => ID, { description: '' })
  @IsNotEmpty()
  userId: string;
}
