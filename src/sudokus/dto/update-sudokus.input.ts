import { CreateSudokusInput } from './create-sudokus.input';
import { InputType, Field, Int, PartialType } from '@nestjs/graphql';

@InputType()
export class UpdateSudokusInput extends PartialType(CreateSudokusInput) {
  @Field(() => Int)
  id: number;
}
