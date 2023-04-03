import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the auth resolver to activate the email address',
})
export class ActivateEmailInput {
  @Field(() => String, { description: 'An authentication token' })
  @IsString()
  @IsNotEmpty()
  token: string;
}
