import { Injectable } from '@nestjs/common';
import { CreateSudokusInput } from './dto/create-sudokus.input';
import { UpdateSudokusInput } from './dto/update-sudokus.input';

@Injectable()
export class SudokusService {
  create(createSudokusInput: CreateSudokusInput) {
    return 'This action adds a new sudokus';
  }

  findAll() {
    return `This action returns all sudokus`;
  }

  findOne(id: number) {
    return `This action returns a #${id} sudokus`;
  }

  update(id: number, updateSudokusInput: UpdateSudokusInput) {
    return `This action updates a #${id} sudokus`;
  }

  remove(id: number) {
    return `This action removes a #${id} sudokus`;
  }
}
