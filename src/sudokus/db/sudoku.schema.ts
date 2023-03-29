import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import mongoose, { HydratedDocument, Types } from 'mongoose';

export type SudokuDocument = HydratedDocument<Sudoku>;

@Schema({ timestamps: true })
export class Sudoku {
  @Prop()
  id?: string;

  @Prop()
  createdAt?: Date;

  @Prop()
  updatedAt?: Date;

  @Prop({ type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true })
  author: Types.ObjectId | string;

  @Prop({ type: String, required: true })
  content: string;

  @Prop({ type: Number, default: 0, required: true, min: 0 })
  favoriteCount: number;

  @Prop({
    type: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        default: [],
        required: true,
      },
    ],
  })
  favoritedBy: (Types.ObjectId | string)[];
}

export const SudokuSchema = SchemaFactory.createForClass(Sudoku);
