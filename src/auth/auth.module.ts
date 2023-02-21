import { Module, forwardRef } from '@nestjs/common';
import { AuthService } from './services/auth.service';
import { AuthResolver } from './auth.resolver';
import { UsersModule } from 'src/users/users.module';
import { PassportModule } from '@nestjs/passport';
import { LocalStrategy } from './strategies/local.strategy';

@Module({
  imports: [forwardRef(() => UsersModule), PassportModule],
  providers: [AuthResolver, AuthService, LocalStrategy],
  exports: [AuthService],
})
export class AuthModule {}

// imports: [
//   forwardRef(() => UsersModule),
//   PassportModule,
//   JwtModule.registerAsync({
//     imports: [ConfigModule],
//     useFactory: async (configService: ConfigService) => {
//       return {
//         secret: configService.get<string>('JWT_SECRET'),
//       };
//     },
//     inject: [ConfigService],
//   }),
// ],
// providers: [AuthService, LocalStrategy, JwtStrategy, SendgridService],
// exports: [AuthService, SendgridService],
// controllers: [AuthController],
// })
// export class AuthModule {}
