import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length, IsEmail } from 'class-validator';
import {
  minUsernameLength,
  maxUsernameLength,
  minPasswordLength,
  maxPasswordLength,
} from 'src/constants';

@InputType({ description: '' })
export class SignInInput {
  @Field(() => String, { description: '' })
  @IsString()
  @Length(minUsernameLength, maxUsernameLength)
  username: string;

  @Field(() => String, { description: '' })
  @IsString()
  @Length(minPasswordLength, maxPasswordLength)
  password: string;
}
