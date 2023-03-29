import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length } from 'class-validator';
import { minPasswordLength, maxPasswordLength } from 'src/constants';

@InputType({ description: '' })
export class ResetPasswordInput {
  @Field(() => String, { description: '' })
  @IsString()
  token: string;

  @Field(() => String, { description: '' })
  @IsString()
  @Length(minPasswordLength, maxPasswordLength)
  password: string;
}
