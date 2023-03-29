import { Module, forwardRef } from '@nestjs/common';
import { SudokusService } from './services/sudokus.service';
import { SudokusResolver } from './resolvers/sudokus.resolver';
import { ValidationService } from './services/validation.service';
import { SudokusRepository } from './db/sudokus.repository';
import { MongooseModule } from '@nestjs/mongoose';
import { AuthModule } from 'src/auth/auth.module';
import { Sudoku, SudokuSchema } from './db/sudoku.schema';
import { UsersModule } from 'src/users/users.module';

@Module({
  providers: [
    SudokusResolver,
    SudokusService,
    ValidationService,
    SudokusRepository,
  ],
  imports: [
    MongooseModule.forFeature([{ name: Sudoku.name, schema: SudokuSchema }]),
    AuthModule,
    forwardRef(() => UsersModule),
  ],
  exports: [SudokusService, SudokusRepository],
})
export class SudokusModule {}
