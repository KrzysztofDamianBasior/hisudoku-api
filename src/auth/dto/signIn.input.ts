import { InputType, Field } from '@nestjs/graphql';
import { IsString, Length, IsEmail } from 'class-validator';

@InputType()
export class SignInInput {
  @IsString()
  @Length(5, 50)
  @Field(() => String, { description: 'Example field (placeholder)' })
  username: string;

  @IsEmail()
  @IsString()
  @Field(() => String, {
    description: 'Example field (placeholder)',
    nullable: true,
  })
  email: string;

  @IsString()
  @Length(5, 50)
  @Field(() => String, { description: 'Example field (placeholder)' })
  password: string;
}
