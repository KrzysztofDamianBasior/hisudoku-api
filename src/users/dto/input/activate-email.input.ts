import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@InputType({ description: '' })
export class ActivateEmailInput {
  @Field(() => String, { description: '' })
  @IsNotEmpty()
  token: string;
}
