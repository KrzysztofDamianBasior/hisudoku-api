import { InputType, Field } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@InputType({ description: '' })
export class UpdateMyPasswordInput {
  @Field(() => String, { description: '' })
  @IsNotEmpty()
  password: string;
}
