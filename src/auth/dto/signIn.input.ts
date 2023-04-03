import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length } from 'class-validator';
import {
  minUsernameLength,
  maxUsernameLength,
  minPasswordLength,
  maxPasswordLength,
} from 'src/constants';

@InputType({
  description: 'The parameters used by the resolver to log in the user.',
})
export class SignInInput {
  @Field(() => String, {
    description: `A name used to identify the user. The name should be longer than ${minUsernameLength} characters and not exceed ${maxUsernameLength} characters.`,
  })
  @IsString()
  @Length(minUsernameLength, maxUsernameLength)
  username: string;

  @Field(() => String, {
    description: `The password used to authenticate the user. The password should be longer than ${minPasswordLength} characters and not exceed ${maxPasswordLength} characters.`,
  })
  @IsString()
  @Length(minPasswordLength, maxPasswordLength)
  password: string;
}
