import { Inject, Injectable, forwardRef } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';

import mongoose, { FilterQuery, Model, Types } from 'mongoose';

import { UsersRepository } from 'src/users/db/users.repository';
import { User as GQLUser } from 'src/users/models/user.model';
import { UserFeed } from 'src/users/models/userFeed.model';

import { Sudoku as DBSudoku, SudokuDocument } from './sudoku.schema';
import { Sudoku as GQLSudoku } from '../models/sudoku.model';
import { SudokuFeed } from '../models/sudokuFeed.model';

@Injectable()
export class SudokusRepository {
  constructor(
    @InjectModel(DBSudoku.name) private sudokuModel: Model<SudokuDocument>,
    @Inject(forwardRef(() => UsersRepository))
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
    // const author = await this.usersRepository.findOneById(authorId);
    const gQLsudoku: GQLSudoku = {
      author: dBSudoku.author,
      id: dBSudoku.id,
      createdAt: dBSudoku.createdAt,
      updatedAt: dBSudoku.updatedAt,
      content,
      favoriteCount: 0,
      favoritedBy: [],
    };
    return gQLsudoku;
  }

  async remove({
    sudokuId,
  }: {
    sudokuId: string | Types.ObjectId;
  }): Promise<GQLSudoku> {
    return this.sudokuModel.findOneAndDelete({ sudokuId });
  }

  async find({
    sudokusFilterQuery,
  }: {
    sudokusFilterQuery: FilterQuery<DBSudoku>;
    // favoritedByCursor: string | null;
    // favoritedByLimit: number;
  }): Promise<GQLSudoku[]> {
    const dBSudokus = await this.sudokuModel.find(sudokusFilterQuery);
    const gQLSudokus: GQLSudoku[] = [];

    for (const sudoku of dBSudokus) {
      // const author = await this.usersRepository.findOneById(sudoku.author);
      // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      //   ids: sudoku.favoritedBy.map((id) => id.toString()),
      //   cursor: favoritedByCursor,
      //   limit: favoritedByLimit,
      // });

      gQLSudokus.push({
        author: sudoku.author,
        id: sudoku.id,
        createdAt: sudoku.createdAt,
        updatedAt: sudoku.updatedAt,
        content: sudoku.content,
        favoriteCount: sudoku.favoriteCount,
        favoritedBy: sudoku.favoritedBy,
      });
    }
    return gQLSudokus;
  }

  async sudokuFeed({
    sudokuCursor,
    sudokusLimit,
  }: {
    sudokuCursor: string | null;
    sudokusLimit: number; //maximum amount of sudoku returned

    // favoritedByLimit: number;
  }): Promise<SudokuFeed> {
    let hasNextPage = false;

    //if no cursor has been passed, the default query will be empty, it will retrieve the latest notes from the database
    let cursorQuery = {};

    //if a cursor has been passed, the query will look for notes whose ObjectId value is less than the cursor value
    if (sudokuCursor) {
      cursorQuery = { _id: { $lt: sudokuCursor } };
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
      // const author = await this.usersRepository.findOneById(sudoku.author);
      // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      //   ids: sudoku.favoritedBy.map((id) => id.toString()),
      //   cursor: null,
      //   limit: favoritedByLimit,
      // });
      gQLSudokus.push({
        author: sudoku.author,
        id: sudoku.id,
        createdAt: sudoku.createdAt,
        updatedAt: sudoku.updatedAt,
        content: sudoku.content,
        favoriteCount: sudoku.favoriteCount,
        favoritedBy: sudoku.favoritedBy,
      });
    }

    return { sudokus: gQLSudokus, cursor: newCursor, hasNextPage };
  }

  async sudokuFeedByAuthor({
    author,
    sudokuCursor,
    sudokusLimit,
  }: {
    author: string;
    sudokuCursor: string | null;
    sudokusLimit: number;
  }): Promise<SudokuFeed> {
    let hasNextPage = false;
    let cursorQuery: any = { author };
    if (sudokuCursor) {
      cursorQuery = { $and: [{ $lt: sudokuCursor }, { author }] };
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
      // const author = await this.usersRepository.findOneById(sudoku.author);
      // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      //   ids: sudoku.favoritedBy.map((id) => id.toString()),
      //   cursor: null,
      //   limit: favoritedByLimit,
      // });
      gQLSudokus.push({
        author: sudoku.author,
        id: sudoku.id,
        createdAt: sudoku.createdAt,
        updatedAt: sudoku.updatedAt,
        content: sudoku.content,
        favoriteCount: sudoku.favoriteCount,
        favoritedBy: sudoku.favoritedBy,
      });
    }

    return { sudokus: gQLSudokus, cursor: newCursor, hasNextPage };
  }

  async findOne({
    sudokuId,
  }: {
    sudokuId: string;
    // favoritedByCursor: string | null;
    // favoritedByLimit: number;
  }): Promise<GQLSudoku> {
    const dBSudoku = await this.sudokuModel.findById(sudokuId);
    // const author = await this.usersRepository.findOneById(dBSudoku.author);
    // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
    //   ids: dBSudoku.favoritedBy.map((id) => id.toString()),
    //   cursor: favoritedByCursor,
    //   limit: favoritedByLimit,
    // });
    const gQLsudoku: GQLSudoku = {
      author: dBSudoku.author,
      id: dBSudoku.id,
      createdAt: dBSudoku.createdAt,
      updatedAt: dBSudoku.updatedAt,
      content: dBSudoku.content,
      favoriteCount: dBSudoku.favoriteCount,
      favoritedBy: dBSudoku.favoritedBy,
    };
    return gQLsudoku;
  }

  async toggleLike({
    sudokuId,
    userId,
  }: {
    sudokuId: string;
    userId: string;
    // favoritedByCursor: string | null;
    // favoritedByLimit: number;
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
      // const author = await this.usersRepository.findOneById(userId);
      // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      //   ids: dBSudoku.favoritedBy.map((id) => id.toString()),
      //   cursor: favoritedByCursor,
      //   limit: favoritedByLimit,
      // });
      const gQLSudoku: GQLSudoku = {
        id: sudokuId,
        author: dBSudoku.author,
        content: dBSudoku.content,
        createdAt: dBSudoku.createdAt,
        favoriteCount: dBSudoku.favoriteCount,
        favoritedBy: dBSudoku.favoritedBy,
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
      // const author = await this.usersRepository.findOneById(userId);
      // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
      //   ids: dBSudoku.favoritedBy.map((id) => id.toString()),
      //   cursor: favoritedByCursor,
      //   limit: favoritedByLimit,
      // });
      const gQLSudoku: GQLSudoku = {
        id: sudokuId,
        author: dBSudoku.author,
        content: dBSudoku.content,
        createdAt: dBSudoku.createdAt,
        favoriteCount: dBSudoku.favoriteCount,
        favoritedBy: dBSudoku.favoritedBy,
        updatedAt: dBSudoku.updatedAt,
      };
      return gQLSudoku;
    }
  }

  async updateContent({
    sudokuId,
    sudokuContent,
  }: {
    sudokuId: string | Types.ObjectId;
    sudokuContent: string;
    // favoritedByCursor: string | null;
    // favoritedByLimit: number;
  }): Promise<GQLSudoku> {
    const sudoku = await this.sudokuModel.findById(sudokuId);
    sudoku.content = sudokuContent;
    const dBSudoku = await sudoku.save();
    // const author = await this.usersRepository.findOneById(sudoku.author);
    // const favoritedBy = await this.usersRepository.findUsersByTheirIds({
    //   ids: dBSudoku.favoritedBy.map((id) => id.toString()),
    //   cursor: favoritedByCursor,
    //   limit: favoritedByLimit,
    // });
    const gQLSudoku: GQLSudoku = {
      id: dBSudoku.id,
      author: dBSudoku.author,
      content: dBSudoku.content,
      createdAt: dBSudoku.createdAt,
      favoriteCount: dBSudoku.favoriteCount,
      favoritedBy: dBSudoku.favoritedBy,
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
  }): Promise<UserFeed> {
    const sudoku = await this.sudokuModel.findById(sudokuId);
    return this.usersRepository.userFeedByTheirIds({
      ids: sudoku.favoritedBy.map((id) => id.toString()),
      cursor: favoritedByCursor,
      limit: favoritedByLimit,
    });
  }

  async findSudokouAuthor({
    sudokuId,
  }: {
    sudokuId: string;
  }): Promise<GQLUser> {
    const sudoku = await this.sudokuModel.findById(sudokuId);
    return this.usersRepository.findOnePublicDetailsById({
      userId: sudoku.author,
    });
  }
}
