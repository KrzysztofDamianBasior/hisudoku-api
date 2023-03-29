import {
  Injectable,
  ForbiddenException,
  Inject,
  forwardRef,
} from '@nestjs/common';
import { UsersService } from 'src/users/services/users.service';

import { SudokusRepository } from '../db/sudokus.repository';

import { SudokuFeedArgs } from '../dto/args/sudoku-feed.args';
import { FindOneSudokuArgs } from '../dto/args/find-one-sudoku.args';
import { RemoveSudokuInput } from '../dto/input/remove-sudoku.input';
import { UpdateSudokuInput } from '../dto/input/update-sudoku.input';
import { CreateSudokuInput } from '../dto/input/create-sudoku.input';
import { ToggleFavoriteSudokuInput } from '../dto/input/toggle-favorite-sudoku.input';

import { User } from 'src/users/models/user.model';
import { UserFeed } from 'src/users/models/userFeed.model';

import { Sudoku } from '../models/sudoku.model';
import { SudokuFeed } from '../models/sudokuFeed.model';
@Injectable()
export class SudokusService {
  constructor(
    private readonly sudokusRepository: SudokusRepository,
    @Inject(forwardRef(() => UsersService))
    private readonly usersService: UsersService,
  ) {}

  async findManySudokusByAuthor({
    author,
    sudokuCursor,
    sudokusLimit,
  }: {
    author: string;
    sudokuCursor: string;
    sudokusLimit: number;
  }): Promise<SudokuFeed> {
    return this.sudokusRepository.sudokuFeedByAuthor({
      author,
      sudokuCursor,
      sudokusLimit,
    });
  }

  async createSudoku({
    authorId,
    createSudokuInput,
  }: {
    authorId: string;
    createSudokuInput: CreateSudokuInput;
  }): Promise<Sudoku> {
    return this.sudokusRepository.create({
      authorId,
      content: createSudokuInput.content,
    });
  }

  async sudokuFeed(sudokuFeedArgs: SudokuFeedArgs): Promise<SudokuFeed> {
    return this.sudokusRepository.sudokuFeed({ ...sudokuFeedArgs });
  }

  async findOneSudoku(findOneSudokuArgs: FindOneSudokuArgs): Promise<Sudoku> {
    return this.sudokusRepository.findOne({ ...findOneSudokuArgs });
  }

  async updateSudokuContent({
    userId,
    updateSudokuInput,
  }: {
    userId: string;
    updateSudokuInput: UpdateSudokuInput;
  }): Promise<Sudoku> {
    await this.verifySudokuAuthor({
      userId,
      sudokuId: updateSudokuInput.sudokuId,
    });
    return this.sudokusRepository.updateContent({ ...updateSudokuInput });
  }

  async remove({
    userId,
    removeSudokuInput,
  }: {
    userId: string;
    removeSudokuInput: RemoveSudokuInput;
  }): Promise<Sudoku> {
    await this.verifySudokuAuthor({
      userId,
      sudokuId: removeSudokuInput.sudokuId,
    });
    return this.sudokusRepository.remove({
      sudokuId: removeSudokuInput.sudokuId,
    });
  }

  async toggleFavorite({
    userId,
    toggleFavoriteSudokuInput,
  }: {
    userId: string;
    toggleFavoriteSudokuInput: ToggleFavoriteSudokuInput;
  }) {
    return this.sudokusRepository.toggleLike({
      userId,
      ...toggleFavoriteSudokuInput,
    });
  }

  async verifySudokuAuthor({
    userId,
    sudokuId,
  }: {
    userId: string;
    sudokuId: string;
  }): Promise<boolean> {
    const sudokuAuthor = await this.sudokusRepository.findSudokouAuthor({
      sudokuId,
    });
    if (sudokuAuthor.id !== userId) {
      throw new ForbiddenException(
        'not enough privileges to perform an action on a resource',
      );
      // throw new GraphQLError(
      //   customMessage,
      //    {
      //     extensions: {
      //     code: customCode,
      //    },
      //   },
      //  );
    }
    return true;
  }

  async findSudokuAuthor({ sudokuId }: { sudokuId: string }): Promise<User> {
    return this.sudokusRepository.findSudokouAuthor({ sudokuId });
  }

  async findUsersWhoLikeSudoku({
    sudokuId,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    sudokuId: string;
    favoritedByCursor: string;
    favoritedByLimit: number;
  }): Promise<UserFeed> {
    return this.sudokusRepository.findUsersWhoLikeSudoku({
      sudokuId,
      favoritedByCursor,
      favoritedByLimit,
    });
  }
}
