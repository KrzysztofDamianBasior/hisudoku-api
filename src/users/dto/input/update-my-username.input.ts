import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@InputType({ description: '' })
export class UpdateMyUsernameInput {
  @Field(() => String, { description: '' })
  @IsNotEmpty()
  newUsername: string;
}
