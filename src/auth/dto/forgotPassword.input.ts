import { InputType, Field } from '@nestjs/graphql';
import { IsString, IsEmail } from 'class-validator';

@InputType()
export class ForgotPasswordInput {
  @IsEmail()
  @IsString()
  @Field(() => String, {
    description: 'Example field (placeholder)',
  })
  email: string;
}
