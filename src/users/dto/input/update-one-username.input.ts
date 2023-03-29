import { InputType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@InputType({ description: '' })
export class UpdateOneUsernameInput {
  @Field(() => ID, { description: '' })
  @IsNotEmpty()
  userId: string;

  @Field(() => String, { description: '' })
  @IsNotEmpty()
  newUsername: string;
}
