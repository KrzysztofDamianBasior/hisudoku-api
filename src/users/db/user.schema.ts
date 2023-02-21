import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type UserDocument = HydratedDocument<User>;
@Schema({ timestamps: true })
export class User {
  @Prop()
  createdAt?: Date;

  @Prop()
  updatedAt?: Date;

  @Prop({ type: String, required: true, unique: true, minlength: 5 })
  username: string;

  @Prop({ type: String, default: '' })
  email: string;

  @Prop({ type: [String], required: true })
  roles: string[];

  @Prop({ type: String, required: true })
  password: string;

  @Prop({ type: String, default: '' })
  resetLink: string;
}

export const UserSchema = SchemaFactory.createForClass(User);
