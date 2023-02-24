import { InputType, Field } from '@nestjs/graphql';

@InputType()
export class CreateSudokuInput {
  @Field(() => String, { description: '' })
  content: string;
}
