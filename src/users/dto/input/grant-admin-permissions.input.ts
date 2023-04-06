import { InputType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the users resolver to grant the user administrator privileges',
})
export class GrantAdminPermissionsInput {
  @Field(() => ID, {
    description: 'ID of the user whose account will be updated',
  })
  @IsNotEmpty()
  @IsString()
  userId: string;
}
