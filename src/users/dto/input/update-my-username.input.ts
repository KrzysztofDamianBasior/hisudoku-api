import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the account owner to update the username',
})
export class UpdateMyUsernameInput {
  @Field(() => String, { description: 'A new username' })
  @IsNotEmpty()
  @IsString()
  newUsername: string;
}
