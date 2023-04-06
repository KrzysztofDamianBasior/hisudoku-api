import { InputType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the users resolver to update the username for one of the accounts',
})
export class UpdateOneUsernameInput {
  @Field(() => ID, {
    description: 'ID of the user whose account will be updated',
  })
  @IsNotEmpty()
  @IsString()
  userId: string;

  @Field(() => String, { description: 'A new username' })
  @IsNotEmpty()
  @IsString()
  newUsername: string;
}
