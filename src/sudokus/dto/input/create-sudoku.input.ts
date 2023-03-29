import { InputType, Field } from '@nestjs/graphql';
import { IsString } from 'class-validator';

@InputType()
export class CreateSudokuInput {
  @Field(() => String, { description: '' })
  @IsString()
  content: string;
}
