import { Field, ObjectType } from '@nestjs/graphql';

@ObjectType({
  description: 'A response returned in the authentication process.',
})
export class AuthResponse {
  @Field(() => String, { description: 'The user authentication token.' })
  access_token: string;
}
