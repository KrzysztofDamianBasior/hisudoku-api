import { Field, ObjectType, ID, GraphQLISODateTime } from '@nestjs/graphql';

@ObjectType({ description: 'The user account information' })
export class User {
  @Field(() => ID, { description: 'The user identifier' })
  id: string;

  @Field(() => GraphQLISODateTime, {
    description: 'The user account creation date',
  })
  createdAt?: Date;

  @Field(() => GraphQLISODateTime, {
    description: 'A date of the last update of the user account',
  })
  updatedAt?: Date;

  @Field(() => String, { description: 'The username' })
  username: string;

  @Field(() => [String], { description: 'The roles assigned to the user' })
  roles: string[];
}
