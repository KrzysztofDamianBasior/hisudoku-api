import { Module } from '@nestjs/common';
import { SudokusService } from './sudokus.service';
import { SudokusResolver } from './sudokus.resolver';

@Module({
  providers: [SudokusResolver, SudokusService]
})
export class SudokusModule {}
