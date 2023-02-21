import { Field, ObjectType, ID, GraphQLISODateTime } from '@nestjs/graphql';

@ObjectType()
export class User {
  @Field(() => ID, { description: 'User identifier' })
  id: string;

  @Field(() => GraphQLISODateTime, { description: 'User identifier' })
  createdAt?: Date;

  @Field(() => GraphQLISODateTime, { description: 'User identifier' })
  updatedAt?: Date;

  @Field(() => String, { description: 'User identifier' })
  username: string;

  @Field(() => String, { description: '', nullable: true })
  email: string;

  @Field(() => [String], { description: '' })
  roles: string[];
}
