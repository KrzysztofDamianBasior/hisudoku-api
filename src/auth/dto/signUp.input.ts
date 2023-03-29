import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length, IsEmail, IsOptional } from 'class-validator';
import {
  minUsernameLength,
  maxUsernameLength,
  minPasswordLength,
  maxPasswordLength,
} from 'src/constants';

@InputType({ description: '' })
export class SignUpInput {
  @Field(() => String, { description: '' })
  @IsString()
  @Length(minUsernameLength, maxUsernameLength)
  username: string;

  @Field(() => String, {
    description: '',
    nullable: true,
  })
  @IsEmail()
  @IsString()
  @IsOptional()
  email: string;

  @Field(() => String, { description: '' })
  @IsString()
  @Length(minPasswordLength, maxPasswordLength)
  password: string;
}
