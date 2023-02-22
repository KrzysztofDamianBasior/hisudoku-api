import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import mongoose, { FilterQuery, Model, Types } from 'mongoose';
import { Sudoku as DBSudoku, SudokuDocument } from './sudoku.schema';
import { UsersRepository } from 'src/users/db/users.repository';
import { Sudoku as GQLSudoku } from '../models/sudoku.model';
import { SudokuFeed } from '../models/sudokuFeed.model';
import { User } from 'src/users/models/user.model';

@Injectable()
export class SudokusRepository {
  constructor(
    @InjectModel(DBSudoku.name) private sudokuModel: Model<SudokuDocument>,
    private readonly usersRepository: UsersRepository,
  ) {}

  async create({
    authorId,
    content,
  }: {
    authorId: string | Types.ObjectId;
    content: string;
  }): Promise<GQLSudoku> {
    const payload: DBSudoku = {
      author: authorId,
      content,
      favoriteCount: 0,
      favoritedBy: [],
    };
    const newSudoku = new this.sudokuModel(payload);
    const dBSudoku = await newSudoku.save();
    const author = await this.usersRepository.findOneById(authorId);
    const gQLsudoku: GQLSudoku = {
      author,
      id: dBSudoku.id,
      createdAt: dBSudoku.createdAt,
      updatedAt: dBSudoku.updatedAt,
      content,
      favoriteCount: 0,
      favoritedBy: [],
    };
    return gQLsudoku;
  }

  async remove(id: string | Types.ObjectId): Promise<GQLSudoku> {
    return this.sudokuModel.findOneAndDelete({ id });
  }

  async find({
    sudokusFilterQuery,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    sudokusFilterQuery: FilterQuery<DBSudoku>;
    favoritedByCursor: string | null;
    favoritedByLimit: number;
  }): Promise<GQLSudoku[]> {
    const dBSudokus = await this.sudokuModel.find(sudokusFilterQuery);
    const gQLSudokus: GQLSudoku[] = [];
    for (const sudoku of dBSudokus) {
      const author = await this.usersRepository.findOneById(sudoku.author);
      const favoritedBy = await this.usersRepository.findUsersByTheirIds({
        ids: sudoku.favoritedBy.map((id) => id.toString()),
        cursor: favoritedByCursor,
        limit: favoritedByLimit,
      });
      gQLSudokus.push({
        author,
        id: sudoku.id,
        createdAt: sudoku.createdAt,
        updatedAt: sudoku.updatedAt,
        content: sudoku.content,
        favoriteCount: sudoku.favoriteCount,
        favoritedBy: favoritedBy,
      });
    }
    return gQLSudokus;
  }

  async findMany({
    sudokusCursor,
    sudokusLimit,
    favoritedByLimit,
  }: {
    sudokusCursor: string | null;
    sudokusLimit: number; //maximum amount of sudoku returned
    favoritedByLimit: number;
  }): Promise<SudokuFeed> {
    let hasNextPage = false;

    //if no cursor has been passed, the default query will be empty, it will retrieve the latest notes from the database
    let cursorQuery = {};

    //if a cursor has been passed, the query will look for notes whose ObjectId value is less than the cursor value
    if (sudokusCursor) {
      cursorQuery = { _id: { $lt: sudokusCursor } };
    }

    let dBSudokus = await this.sudokuModel
      .find(cursorQuery)
      .sort({ _id: -1 })
      .limit(sudokusLimit + 1);

    if (dBSudokus.length > sudokusLimit) {
      hasNextPage = true;
      dBSudokus = dBSudokus.slice(0, -1);
    }

    //the cursor is the mongo identifier of the last element in the array
    const newCursor = dBSudokus[dBSudokus.length - 1].id;
    const gQLSudokus: GQLSudoku[] = [];
    for (const sudoku of dBSudokus) {
      const author = await this.usersRepository.findOneById(sudoku.author);
      const favoritedBy = await this.usersRepository.findUsersByTheirIds({
        ids: sudoku.favoritedBy.map((id) => id.toString()),
        cursor: null,
        limit: favoritedByLimit,
      });
      gQLSudokus.push({
        author,
        id: sudoku.id,
        createdAt: sudoku.createdAt,
        updatedAt: sudoku.updatedAt,
        content: sudoku.content,
        favoriteCount: sudoku.favoriteCount,
        favoritedBy: favoritedBy,
      });
    }

    return { sudokus: gQLSudokus, cursor: newCursor, hasNextPage };
  }

  async findOne({
    id,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    id: string;
    favoritedByCursor: string | null;
    favoritedByLimit: number;
  }): Promise<GQLSudoku> {
    const dBSudoku = await this.sudokuModel.findById(id);
    const author = await this.usersRepository.findOneById(dBSudoku.author);
    const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      ids: dBSudoku.favoritedBy.map((id) => id.toString()),
      cursor: favoritedByCursor,
      limit: favoritedByLimit,
    });
    const gQLsudoku: GQLSudoku = {
      author,
      id: dBSudoku.id,
      createdAt: dBSudoku.createdAt,
      updatedAt: dBSudoku.updatedAt,
      content: dBSudoku.content,
      favoriteCount: dBSudoku.favoriteCount,
      favoritedBy: favoritedBy,
    };
    return gQLsudoku;
  }

  async toggleLike({
    sudokuId,
    userId,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    sudokuId: string;
    userId: string;
    favoritedByCursor: string | null;
    favoritedByLimit: number;
  }): Promise<GQLSudoku> {
    const sudoku = await this.sudokuModel.findById(sudokuId);
    const hasUser = sudoku.favoritedBy
      .map((id) => id.toString())
      .indexOf(userId);
    if (hasUser >= 0) {
      const dBSudoku = await this.sudokuModel.findByIdAndUpdate(
        sudokuId,
        {
          $pull: { favoritedBy: new mongoose.Types.ObjectId(userId) },
          $inc: { favoriteCount: -1 },
        },
        { new: true },
      );
      const author = await this.usersRepository.findOneById(userId);
      const favoritedBy = await this.usersRepository.findUsersByTheirIds({
        ids: dBSudoku.favoritedBy.map((id) => id.toString()),
        cursor: favoritedByCursor,
        limit: favoritedByLimit,
      });
      const gQLSudoku: GQLSudoku = {
        id: sudokuId,
        author,
        content: dBSudoku.content,
        createdAt: dBSudoku.createdAt,
        favoriteCount: dBSudoku.favoriteCount,
        favoritedBy: favoritedBy,
        updatedAt: dBSudoku.updatedAt,
      };
      return gQLSudoku;
    } else {
      const dBSudoku = await this.sudokuModel.findByIdAndUpdate(
        sudokuId,
        {
          $push: { favoritedBy: new mongoose.Types.ObjectId(userId) },
          $inc: { favoriteCount: 1 },
        },
        { new: true },
      );
      const author = await this.usersRepository.findOneById(userId);
      const favoritedBy = await this.usersRepository.findUsersByTheirIds({
        ids: dBSudoku.favoritedBy.map((id) => id.toString()),
        cursor: favoritedByCursor,
        limit: favoritedByLimit,
      });
      const gQLSudoku: GQLSudoku = {
        id: sudokuId,
        author,
        content: dBSudoku.content,
        createdAt: dBSudoku.createdAt,
        favoriteCount: dBSudoku.favoriteCount,
        favoritedBy: favoritedBy,
        updatedAt: dBSudoku.updatedAt,
      };
      return gQLSudoku;
    }
  }

  async updateContent({
    id,
    content,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    id: string | Types.ObjectId;
    content: string;
    favoritedByCursor: string | null;
    favoritedByLimit: number;
  }): Promise<GQLSudoku> {
    const sudoku = await this.sudokuModel.findById(id);
    sudoku.content = content;
    const dBSudoku = await sudoku.save();
    const author = await this.usersRepository.findOneById(sudoku.author);
    const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      ids: dBSudoku.favoritedBy.map((id) => id.toString()),
      cursor: favoritedByCursor,
      limit: favoritedByLimit,
    });
    const gQLSudoku: GQLSudoku = {
      id: dBSudoku.id,
      author,
      content: dBSudoku.content,
      createdAt: dBSudoku.createdAt,
      favoriteCount: dBSudoku.favoriteCount,
      favoritedBy: favoritedBy,
      updatedAt: dBSudoku.updatedAt,
    };
    return gQLSudoku;
  }

  async findUsersWhoLikeSudoku({
    sudokuId,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    sudokuId: string;
    favoritedByCursor: string | null;
    favoritedByLimit: number;
  }): Promise<User[]> {
    const sudoku = await this.sudokuModel.findById(sudokuId);
    return this.usersRepository.findUsersByTheirIds({
      ids: sudoku.favoritedBy.map((id) => id.toString()),
      cursor: favoritedByCursor,
      limit: favoritedByLimit,
    });
  }

  async findSudokouAuthor(sudokuId: string): Promise<User> {
    const sudoku = await this.sudokuModel.findById(sudokuId);
    return this.usersRepository.findOne({
      _id: sudoku.author,
    });
  }
}
