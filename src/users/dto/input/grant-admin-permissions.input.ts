import { InputType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty } from 'class-validator';

@InputType({ description: '' })
export class GrantAdminPermissionsInput {
  @Field(() => ID, { description: '' })
  @IsNotEmpty()
  userId: string;
}
