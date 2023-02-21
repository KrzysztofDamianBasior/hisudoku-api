import { Field, ObjectType, ID, GraphQLISODateTime } from '@nestjs/graphql';
import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type UserDocument = HydratedDocument<User>;
@ObjectType()
@Schema({ timestamps: true })
export class User {
  @Field(() => ID, { description: 'User identifier' })
  @Prop()
  id: string;

  @Field(() => GraphQLISODateTime, { description: 'User identifier' })
  @Prop()
  createdAt?: Date;

  @Field(() => GraphQLISODateTime, { description: 'User identifier' })
  @Prop()
  updatedAt?: Date;

  @Field(() => String, { description: 'User identifier' })
  @Prop({ type: String, required: true, unique: true, minlength: 5 })
  username: string;

  @Field(() => String, { description: '', nullable: true })
  @Prop({ type: String, default: '' })
  email: string;

  @Field(() => [String], { description: '' })
  @Prop({ type: [String], required: true })
  roles: string[];

  @Prop({ type: String, required: true })
  password: string;

  @Prop({ type: String, default: '' })
  resetLink: string;
}

export const UserSchema = SchemaFactory.createForClass(User);
