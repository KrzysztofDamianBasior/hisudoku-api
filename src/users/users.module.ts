import { Module, forwardRef } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { UsersService } from './services/users.service';

import { UsersResolver } from './resolvers/users.resolver';
import { MyAccountResolver } from './resolvers/myAccount.resolver';

import { UsersRepository } from './db/users.repository';
import { User, UserSchema } from './db/user.schema';

import { AuthModule } from 'src/auth/auth.module';

import { SudokusModule } from 'src/sudokus/sudokus.module';

@Module({
  providers: [UsersResolver, MyAccountResolver, UsersService, UsersRepository],
  imports: [
    MongooseModule.forFeature([{ name: User.name, schema: UserSchema }]),
    forwardRef(() => AuthModule),
    forwardRef(() => SudokusModule),
  ],
  exports: [UsersService, UsersRepository],
})
export class UsersModule {}
