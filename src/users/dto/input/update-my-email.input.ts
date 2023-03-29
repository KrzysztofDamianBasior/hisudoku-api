import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@InputType({ description: '' })
export class UpdateMyEmailInput {
  @Field(() => String, { description: '' })
  @IsNotEmpty()
  email: string;
}
