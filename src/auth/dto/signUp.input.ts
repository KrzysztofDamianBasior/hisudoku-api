import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length, IsEmail, IsOptional } from 'class-validator';
import {
  minUsernameLength,
  maxUsernameLength,
  minPasswordLength,
  maxPasswordLength,
  minEmailLength,
  maxEmailLength,
} from 'src/constants';

@InputType({
  description:
    'The parameters used by the auth resolver to create a new user account.',
})
export class SignUpInput {
  @Field(() => String, {
    description: `A name used to identify the user. The name should be longer than ${minUsernameLength} characters and not exceed ${maxUsernameLength} characters.`,
  })
  @IsString()
  @Length(minUsernameLength, maxUsernameLength)
  username: string;

  @Field(() => String, {
    description: `An optional e-mail address used in the password recovery process. The email should be longer than ${minEmailLength} characters and not exceed ${maxEmailLength} characters.`,
    nullable: true,
  })
  @IsEmail()
  @IsString()
  @IsOptional()
  @Length(minEmailLength, maxEmailLength)
  email: string;

  @Field(() => String, {
    description: `The password used to authenticate the user. The password should be longer than ${minPasswordLength} characters and not exceed ${maxPasswordLength} characters.`,
  })
  @IsString()
  @Length(minPasswordLength, maxPasswordLength)
  password: string;
}
