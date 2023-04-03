import { Field, ObjectType } from '@nestjs/graphql';

@ObjectType({
  description: 'A structure with information or instructions',
})
export class MessageResponse {
  @Field(() => String, {
    description: 'A message with process status information',
  })
  message: string;
}
