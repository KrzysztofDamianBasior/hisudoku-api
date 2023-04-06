import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the account owner to update the password',
})
export class UpdateMyPasswordInput {
  @Field(() => String, { description: 'A new password' })
  @IsNotEmpty()
  @IsString()
  newPassword: string;
}
