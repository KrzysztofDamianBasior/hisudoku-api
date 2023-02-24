import { Test, TestingModule } from '@nestjs/testing';
import { SudokusService } from './sudokus.service';

describe('SudokusService', () => {
  let service: SudokusService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [SudokusService],
    }).compile();

    service = module.get<SudokusService>(SudokusService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
