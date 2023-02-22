import { Test, TestingModule } from '@nestjs/testing';
import { SudokusResolver } from './sudokus.resolver';
import { SudokusService } from './sudokus.service';

describe('SudokusResolver', () => {
  let resolver: SudokusResolver;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [SudokusResolver, SudokusService],
    }).compile();

    resolver = module.get<SudokusResolver>(SudokusResolver);
  });

  it('should be defined', () => {
    expect(resolver).toBeDefined();
  });
});
