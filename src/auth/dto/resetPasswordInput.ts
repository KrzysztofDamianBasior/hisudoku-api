import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length, IsEmail } from 'class-validator';

@InputType()
export class ResetPasswordInput {
  @IsString()
  @Field(() => String, { description: 'Example field (placeholder)' })
  token: string;

  @IsString()
  @Length(5, 50)
  @Field(() => String, { description: 'Example field (placeholder)' })
  password: string;
}
