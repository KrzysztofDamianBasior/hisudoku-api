import { InputType, Int, Field } from '@nestjs/graphql';

@InputType()
export class CreateSudokusInput {
  @Field(() => Int, { description: 'Example field (placeholder)' })
  exampleField: number;
}
