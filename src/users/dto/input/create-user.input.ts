import { InputType, Int, Field } from '@nestjs/graphql';

@InputType()
export class CreateUserInput {
  @Field(() => Int, { description: 'Example field (placeholder)' })
  exampleField: number;
}

// @InputType()
// export class UpdateUserInput {
//     @Field()
//     @IsNotEmpty()
//     userId: string;

//     @Field()
//     @IsOptional()
//     @IsNotEmpty()
//     age?: number;

//     @Field({ nullable: true })
//     @IsOptional()
//     isSubscribed?: boolean
// }
