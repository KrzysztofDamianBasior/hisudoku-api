import { InputType, Field, ID } from '@nestjs/graphql';
import { IsNotEmpty, IsString } from 'class-validator';

@InputType({
  description:
    'The parameters used by the users resolver to remove one user account',
})
export class RemoveOneInput {
  @Field(() => ID, {
    description: 'ID of the user whose account will be updated',
  })
  @IsNotEmpty()
  @IsString()
  userId: string;
}
