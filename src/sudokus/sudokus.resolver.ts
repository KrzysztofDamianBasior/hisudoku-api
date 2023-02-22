import { Resolver, Query, Mutation, Args, Int } from '@nestjs/graphql';
import { SudokusService } from './sudokus.service';
import { Sudokus } from './entities/sudokus.entity';
import { CreateSudokusInput } from './dto/create-sudokus.input';
import { UpdateSudokusInput } from './dto/update-sudokus.input';

@Resolver(() => Sudokus)
export class SudokusResolver {
  constructor(private readonly sudokusService: SudokusService) {}

  @Mutation(() => Sudokus)
  createSudokus(@Args('createSudokusInput') createSudokusInput: CreateSudokusInput) {
    return this.sudokusService.create(createSudokusInput);
  }

  @Query(() => [Sudokus], { name: 'sudokus' })
  findAll() {
    return this.sudokusService.findAll();
  }

  @Query(() => Sudokus, { name: 'sudokus' })
  findOne(@Args('id', { type: () => Int }) id: number) {
    return this.sudokusService.findOne(id);
  }

  @Mutation(() => Sudokus)
  updateSudokus(@Args('updateSudokusInput') updateSudokusInput: UpdateSudokusInput) {
    return this.sudokusService.update(updateSudokusInput.id, updateSudokusInput);
  }

  @Mutation(() => Sudokus)
  removeSudokus(@Args('id', { type: () => Int }) id: number) {
    return this.sudokusService.remove(id);
  }
}
