import {
  BadRequestException,
  Inject,
  Injectable,
  forwardRef,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { GraphQLError } from 'graphql';
import * as bcrypt from 'bcrypt';

import { SudokusService } from 'src/sudokus/services/sudokus.service';
import { AuthService } from 'src/auth/services/auth.service';
import { Role } from 'src/auth/roles';
import { UsersRepository } from '../db/users.repository';

import { User as GQLUser } from '../models/user.model';
import { MyAccount } from '../models/myAccount.model';
import { UserFeed } from '../models/userFeed.model';

import { FindOneUserArgs } from '../dto/args/find-one-user.args';
import { UserFeedArgs } from '../dto/args/user-feed.args';
import { GrantAdminPermissionsInput } from '../dto/input/grant-admin-permissions.input';

export type MyAccountWithoutNestedFields = Omit<MyAccount, 'createdSudokus'>;

@Injectable()
export class UsersService {
  constructor(
    private readonly usersRepository: UsersRepository,
    private readonly configService: ConfigService,
    @Inject(forwardRef(() => AuthService))
    private readonly authService: AuthService,
    @Inject(forwardRef(() => SudokusService))
    private readonly sudokusService: SudokusService,
  ) {}

  async removeMyAccountById({
    userId,
  }: {
    userId: string;
  }): Promise<MyAccountWithoutNestedFields> {
    return this.usersRepository.removeOneById({ userId });
  }

  async removeOne({ userId }: { userId: string }): Promise<GQLUser> {
    const user = await this.usersRepository.removeOneById({ userId });
    return {
      id: user.id,
      roles: user.roles,
      username: user.username,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
  }

  async activateEmail({
    newEmail,
    userId,
  }: {
    newEmail: string;
    userId: string;
  }): Promise<MyAccountWithoutNestedFields> {
    const doesEmailExistInTheDB = await this.usersRepository.doesEmailExist({
      email: newEmail,
    });
    if (doesEmailExistInTheDB) {
      // throw new GraphQLError(
      throw new BadRequestException(
        'this email address already exists in the database, the email address for each account must be unique',
      );
    }
    const user = await this.usersRepository.updateOneEmail({
      userId,
      newEmail: newEmail,
    });
    return user;
  }

  async updateMyEmail({
    newEmail,
    userId,
  }: {
    newEmail: string;
    userId: string;
  }): Promise<MyAccountWithoutNestedFields> {
    const doesEmailExistInTheDB = await this.usersRepository.doesEmailExist({
      email: newEmail,
    });
    if (doesEmailExistInTheDB) {
      // throw new GraphQLError(
      throw new BadRequestException(
        'this email address already exists in the database, the email address for each account must be unique',
      );
    }
    const user = await this.usersRepository.updateOneEmail({
      userId,
      newEmail: 'waiting for verification',
    });
    await this.authService.sendActivateEmailLink({
      username: user.username,
      email: newEmail,
      id: user.id,
    });
    return user;
  }

  async updateMyUsername({
    userId,
    newUsername,
  }: {
    userId: string;
    newUsername: string;
  }): Promise<MyAccountWithoutNestedFields> {
    const doesUsernameExistInTheDB =
      await this.usersRepository.doesUsernameExist({ username: newUsername });
    if (doesUsernameExistInTheDB) {
      throw new BadRequestException('this username is already taken');
    }
    return this.usersRepository.updateOneUsername({ userId, newUsername });
  }

  async updateOneUsername({
    newUsername,
    userId,
  }: {
    newUsername: string;
    userId: string;
  }): Promise<GQLUser> {
    const doesUsernameExistInTheDB =
      await this.usersRepository.doesUsernameExist({ username: newUsername });
    if (doesUsernameExistInTheDB) {
      throw new BadRequestException('this username is already taken');
    }
    const user = await this.usersRepository.updateOneUsername({
      userId,
      newUsername,
    });
    return {
      id: user.id,
      roles: user.roles,
      username: user.username,
      createdAt: user.createdAt,
      updatedAt: user.createdAt,
    };
  }

  async createOne({
    username,
    plainTextPassword,
    roles,
    email,
  }: {
    username: string;
    plainTextPassword: string;
    roles: Role[];
    email?: string;
  }): Promise<MyAccountWithoutNestedFields> {
    const doesUsernameExistInTheDB =
      await this.usersRepository.doesUsernameExist({ username });
    if (doesUsernameExistInTheDB) {
      // throw new GraphQLError
      throw new BadRequestException('this username is already taken');
    }

    const salt = await bcrypt.genSalt(
      this.configService.get<number>('SALT_LENGTH'),
    );
    const hashedPassword = await bcrypt.hash(plainTextPassword, salt);
    const payload = {
      username,
      email: '',
      roles,
      hashedPassword: hashedPassword,
      resetLink: '',
    };
    if (email) {
      const doesEmailExistInTheDB = await this.usersRepository.doesEmailExist({
        email,
      });
      if (doesEmailExistInTheDB) {
        throw new BadRequestException(
          'this email address already exists in the database, the email address for each account must be unique',
        );
      }
      payload.email = 'waiting for verification';
      const newUser = await this.usersRepository.createOne(payload);
      await this.authService.sendActivateEmailLink({
        email: email,
        username: newUser.username,
        id: newUser.id,
      });
      return newUser;
    } else {
      return await this.usersRepository.createOne(payload);
    }
  }

  async doesUsernameExist({ username }: { username: string }) {
    return this.usersRepository.doesUsernameExist({ username });
  }

  async doesEmailExist({ email }: { email: string }) {
    return this.usersRepository.doesEmailExist({ email });
  }

  async findPasswordByUsername({
    username,
  }: {
    username: string;
  }): Promise<{ password: string }> {
    return this.usersRepository.findOnePrivateDetailsByUsername({ username });
  }

  async findManyByTheirIds({
    ids,
    cursor,
    limit,
  }: {
    ids: string[];
    cursor: string;
    limit: number;
  }): Promise<UserFeed> {
    return this.usersRepository.userFeedByTheirIds({ ids, cursor, limit });
  }

  async findOneById({ userId }: FindOneUserArgs): Promise<GQLUser> {
    return this.usersRepository.findOnePublicDetailsById({ userId });
  }

  async userFeed({ userCursor, usersLimit }: UserFeedArgs): Promise<UserFeed> {
    return this.usersRepository.userFeed({
      cursor: userCursor,
      limit: usersLimit,
    });
  }

  async findMyAccountById({
    userId,
  }: {
    userId: string;
  }): Promise<MyAccountWithoutNestedFields> {
    return this.usersRepository.findOneOpenDetailsById({ userId });
  }

  async grantAdminPermissions({
    userId,
  }: GrantAdminPermissionsInput): Promise<GQLUser> {
    return this.usersRepository.updateOneRoles({
      userId,
      newRoles: ['User', 'Admin'],
    });
  }

  async findOneByUsername({
    username,
  }: {
    username: string;
  }): Promise<GQLUser> {
    return this.usersRepository.findOnePublicDetailsByUsername({ username });
  }

  async findMyAccountByEmail({
    email,
  }: {
    email: string;
  }): Promise<MyAccountWithoutNestedFields> {
    return this.usersRepository.findOneOpenDetailsByEmail({ email });
  }

  async findOneByResetPasswordLink({
    resetPasswordLink,
  }: {
    resetPasswordLink: string;
  }): Promise<GQLUser> {
    return this.usersRepository.findOnePublicDetailsByResetPasswordLink({
      resetPasswordLink,
    });
  }

  async all({
    offset,
    perPage,
  }: {
    offset: number;
    perPage: number;
  }): Promise<GQLUser[]> {
    return this.usersRepository.findMany({
      usersFilterQuery: {},
      offset,
      perPage,
    });
  }

  async updatePasswordAndRemoveResetPasswordLink({
    resetPasswordLink,
    password,
  }: {
    resetPasswordLink: string;
    password: string;
  }): Promise<MyAccountWithoutNestedFields> {
    const salt = await bcrypt.genSalt(
      this.configService.get<number>('SALT_LENGTH'),
    );
    return this.usersRepository.updateOnePasswordAndRemoveResetPasswordLink({
      newPassword: await bcrypt.hash(password, salt),
      resetPasswordLink,
    });
  }

  async updateResetPasswordLink({
    resetPasswordLink,
    userId,
  }: {
    resetPasswordLink: string;
    userId: string;
  }): Promise<GQLUser> {
    return this.usersRepository.updateResetPasswordLink({
      userId,
      newResetPasswordLink: resetPasswordLink,
    });
  }

  async updateMyPassword({
    userId,
    password,
  }: {
    userId: string;
    password: string;
  }): Promise<MyAccountWithoutNestedFields> {
    const salt = await bcrypt.genSalt(
      this.configService.get<number>('SALT_LENGTH'),
    );
    const hashedPassword = await bcrypt.hash(password, salt);

    return this.usersRepository.updateOnePassword({
      userId,
      newPassword: hashedPassword,
    });
  }

  async forceEdit(
    id: string,
    {
      email,
      plainTextPassword,
      username,
      resetLink,
    }: {
      username?: string;
      email?: string;
      plainTextPassword?: string;
      resetLink?: string;
    },
  ): Promise<MyAccountWithoutNestedFields> {
    const update: { [key: string]: unknown } = {};
    if (username) update.username = username;
    if (email) {
      update.email = email;
    }
    if (plainTextPassword) {
      const salt = await bcrypt.genSalt(
        this.configService.get<number>('SALT_LENGTH'),
      );
      const hashedPassword = await bcrypt.hash(plainTextPassword, salt);
      update.password = hashedPassword;
    }
    if (resetLink || resetLink === '') update.resetLink = resetLink;

    return this.usersRepository.forceUpdate({
      user: { id: id },
      userFilterQuery: update,
    });
  }
}
