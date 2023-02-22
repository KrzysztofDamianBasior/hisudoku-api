import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { GraphQLModule } from '@nestjs/graphql';
import { ApolloDriver, ApolloDriverConfig } from '@nestjs/apollo';
import { join } from 'path';
import { validate } from './env.validation';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import { UsersModule } from './users/users.module';
import { AuthModule } from './auth/auth.module';
import { SudokusModule } from './sudokus/sudokus.module';

@Module({
  imports: [
    GraphQLModule.forRoot<ApolloDriverConfig>({
      driver: ApolloDriver,
      autoSchemaFile: join(process.cwd(), 'src/schema.gql'),
    }),
    ConfigModule.forRoot({
      validate,
      isGlobal: true,
    }),
    MongooseModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: async (config: ConfigService) => ({
        uri: config.get<string>('MONGODB_URI'),
      }),
    }),
    UsersModule,
    AuthModule,
    SudokusModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}

// constructor(private configService: ConfigService<EnvironmentVariables>
// const port = this.configService.get('PORT', { infer: true });
// const dbUser = this.configService.get<string>('DATABASE_USER');

//   scalar DateTime

//   type User {
//     id: ID!
//     username: String!
//     sudokus: [Sudoku!]!
//     favorites: [Sudoku!]!
//   }

//   type Me {
//     id: ID!
//     username: String!
//     email: String
//     sudokus: [Sudoku!]!
//     favorites: [Sudoku!]!
//   }

//   type Sudoku {
//     id: ID!
//     content: String!
//     author: User!
//     createdAt: DateTime!
//     updatedAt: DateTime!
//     favoriteCount: Int!
//     favoritedBy: [User!]!
//   }

//   type SudokuFeed {
//     sudokus: [Sudoku!]!
//     cursor: String!
//     hasNextPage: Boolean!
//   }

//   type Query {
//     sudokus: [Sudoku!]!
//     sudoku(id: ID): Sudoku!
//     user(username: String!): User
//     users: [User!]!
//     me: Me!
//     sudokuFeed(cursor: String): SudokuFeed
//   }

//   type Mutation {
//     newSudoku(content: String!): Sudoku!
//     deleteSudoku(id: ID!): Boolean!
//     updateSudoku(id: ID!, content: String!): Sudoku!

//     signUp(username: String!, email: String, password: String!): String
//     signIn(username: String, email: String, password: String!): String!

//     updateUserUsername(username: String!): String!
//     updateUserEmail(email: String!): String!
//     updateUserPassword(password: String!): Boolean!

//     activateEmail(token: String!): Boolean!
//     forgotPassword(email: String!): Boolean!
//     resetPassword(token: String!, newPassword: String!): Boolean!

//     toggleFavorite(id: ID!): Sudoku!
//   }
