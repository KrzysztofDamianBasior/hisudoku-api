import { ObjectType, Field, Int } from '@nestjs/graphql';

@ObjectType()
export class Sudokus {
  @Field(() => Int, { description: 'Example field (placeholder)' })
  exampleField: number;
}
