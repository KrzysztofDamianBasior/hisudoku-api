import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length } from 'class-validator';
import { minPasswordLength, maxPasswordLength } from 'src/constants';

@InputType({
  description:
    "The parameters used by the auth resolver to reset the user's password",
})
export class ResetPasswordInput {
  @Field(() => String, { description: 'An authentication token' })
  @IsString()
  token: string;

  @Field(() => String, {
    description: `A new password used to authenticate the user. The password should be longer than ${minPasswordLength} characters and not exceed ${maxPasswordLength} characters.`,
  })
  @IsString()
  @Length(minPasswordLength, maxPasswordLength)
  newPassword: string;
}
