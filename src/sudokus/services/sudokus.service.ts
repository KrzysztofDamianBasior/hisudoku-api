import {
  Injectable,
  ForbiddenException,
  Inject,
  forwardRef,
} from '@nestjs/common';
import { CreateSudokuInput } from '../dto/input/create-sudoku.input';
import { UpdateSudokuInput } from '../dto/input/update-sudoku.input';
import { UsersService } from 'src/users/services/users.service';
import { AuthService } from 'src/auth/services/auth.service';
import { SudokusRepository } from '../db/sudokus.repository';
import { Sudoku } from '../models/sudoku.model';
import { FindManySudokusArgs } from '../dto/args/find-many-sudokus.args';
import { SudokuFeed } from '../models/sudokuFeed.model';
import { FindOneSudokuArgs } from '../dto/args/find-one-sudoku.args';
import { RemoveSudokuInput } from '../dto/input/remove-sudoku.input';
import { ToggleFavoriteSudokuInput } from '../dto/input/toggle-favorite-sudoku.input';
import { User } from 'src/users/models/user.model';

@Injectable()
export class SudokusService {
  constructor(
    private readonly sudokusRepository: SudokusRepository,
    @Inject(forwardRef(() => UsersService))
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
  ) {}

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

  async findManySudokus(
    findManySudokusArgs: FindManySudokusArgs,
  ): Promise<SudokuFeed> {
    return this.sudokusRepository.findMany({ ...findManySudokusArgs });
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
    this.verifyNoteAuthor({ userId, sudokuId: updateSudokuInput.sudokuId });
    return this.sudokusRepository.updateContent({ ...updateSudokuInput });
  }

  async remove({
    userId,
    removeSudokuInput,
  }: {
    userId: string;
    removeSudokuInput: RemoveSudokuInput;
  }): Promise<Sudoku> {
    this.verifyNoteAuthor({ userId, sudokuId: removeSudokuInput.sudokuId });
    return this.sudokusRepository.remove(removeSudokuInput.sudokuId);
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

  async verifyNoteAuthor({
    userId,
    sudokuId,
  }: {
    userId: string;
    sudokuId: string;
  }): Promise<boolean> {
    const sudokuAuthor = await this.sudokusRepository.findSudokouAuthor(
      sudokuId,
    );
    if (sudokuAuthor.id !== userId) {
      throw new ForbiddenException(
        'not enough privileges to perform an action on a resource',
      );
    }
    return true;
  }

  async findSudokuAuthor(sudokuId: string): Promise<User> {
    return this.sudokusRepository.findSudokouAuthor(sudokuId);
  }

  async findUsersWhoLikeSudoku({
    sudokuId,
    favoritedByCursor,
    favoritedByLimit,
  }: {
    sudokuId: string;
    favoritedByCursor: string;
    favoritedByLimit: number;
  }): Promise<User[]> {
    return this.sudokusRepository.findUsersWhoLikeSudoku({
      sudokuId,
      favoritedByCursor,
      favoritedByLimit,
    });
  }
}
