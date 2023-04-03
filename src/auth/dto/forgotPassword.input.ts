import { InputType, Field } from '@nestjs/graphql';
import { IsString, IsEmail, Length } from 'class-validator';
import { maxEmailLength, minEmailLength } from 'src/constants';

@InputType({
  description:
    'The parameters used by the resolver to start the password recovery procedure',
})
export class ForgotPasswordInput {
  @Field(() => String, {
    description: `An e-mail address used in the password recovery process. The email should be longer than ${minEmailLength} characters and not exceed ${maxEmailLength} characters.`,
  })
  @IsEmail()
  @IsString()
  @Length(minEmailLength, maxEmailLength)
  email: string;
}
