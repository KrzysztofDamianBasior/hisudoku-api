import {
  BadRequestException,
  Inject,
  Injectable,
  forwardRef,
} from '@nestjs/common';
import { UsersRepository } from '../db/users.repository';
import { User as DBUser } from '../db/user.schema';
import { User as GQLUser } from '../models/user.model';
import * as bcrypt from 'bcrypt';
import { AuthService } from 'src/auth/services/auth.service';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class UsersService {
  constructor(
    private readonly usersRepository: UsersRepository,
    @Inject(forwardRef(() => AuthService))
    private readonly authService: AuthService,
    private readonly configService: ConfigService,
  ) {}

  async create({
    username,
    plainTextPassword,
    email,
  }: {
    username: string;
    plainTextPassword: string;
    email?: string;
  }): Promise<GQLUser> {
    const salt = await bcrypt.genSalt(
      this.configService.get<number>('SALT_LENGTH'),
    );
    const hashedPassword = await bcrypt.hash(plainTextPassword, salt);
    const user: DBUser = {
      username,
      email: '',
      password: hashedPassword,
      resetLink: '',
      roles: ['User'],
    };
    if (email && email.length > 3) {
      user.email = 'waiting for verification';
      const newUser = await this.usersRepository.createOne(user);
      return newUser;
      // token = sendActivateLink
      // return this.usersRepository.updateResetLink
    } else {
      return await this.usersRepository.createOne(user);
    }
  }

  async sendActivateLinkAndSetResetLink({
    sub,
    username,
    email,
  }: {
    sub: string;
    username: string;
    email: string;
  }): Promise<GQLUser> {
    // const resetLink = await this.authService.sendActivateLink({
    //   id: sub,
    //   username,
    //   email,
    // });
    //return this.usersRepository.updateResetLink({ resetLink, id: sub });
    return null;
  }

  async findPasswordByUsername(username: string): Promise<{
    password: string;
  }> {
    return this.usersRepository.findPasswordByUsername(username);
  }

  async remove(userId: string): Promise<GQLUser> {
    return this.usersRepository.removeById(userId);
  }

  async grantAdminPermissions(id): Promise<GQLUser> {
    return this.usersRepository.updateRoles({ id, roles: ['User', 'Admin'] });
  }

  async findById(id: string): Promise<GQLUser> {
    return this.usersRepository.findOneById(id);
  }

  async findByUsername(username: string): Promise<GQLUser> {
    return this.usersRepository.findOne({ username });
  }

  async findByEmail(email: string): Promise<GQLUser> {
    return this.usersRepository.findOne({ email });
  }

  async findByResetLink(resetLink: string): Promise<GQLUser> {
    return this.usersRepository.findOne({ resetLink });
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
  async updateEmailAndRemoveResetLink({
    resetLink,
    email,
  }: {
    resetLink: string;
    email: string;
  }): Promise<GQLUser> {
    return this.usersRepository.updateEmailAndRemoveResetLink({
      email,
      resetLink,
    });
  }
  async updatePasswordAndRemoveResetLink({
    resetLink,
    password,
  }: {
    resetLink: string;
    password: string;
  }): Promise<GQLUser> {
    const salt = await bcrypt.genSalt(
      this.configService.get<number>('SALT_LENGTH'),
    );
    return this.usersRepository.updatePasswordAndRemoveResetLink({
      password: await bcrypt.hash(password, salt),
      resetLink,
    });
  }

  async updateResetLink({
    resetLink,
    id,
  }: {
    resetLink: string;
    id: string;
  }): Promise<GQLUser> {
    return this.usersRepository.updateResetLink({ id, resetLink });
  }

  async updatePassword({
    id,
    plainTextPassword,
  }: {
    id: string;
    plainTextPassword: string;
  }): Promise<GQLUser> {
    const salt = await bcrypt.genSalt(
      this.configService.get<number>('SALT_LENGTH'),
    );
    const hashedPassword = await bcrypt.hash(plainTextPassword, salt);

    return this.usersRepository.updatePassword({
      id,
      password: hashedPassword,
    });
  }

  async updateUsername({
    id,
    username,
  }: {
    id: string;
    username: string;
  }): Promise<GQLUser> {
    if (this.usersRepository.isUsernameExist(username)) {
      throw new BadRequestException('this username is already taken');
    }
    return this.usersRepository.updateUsername({ id, username });
  }

  async edit(
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
  ): Promise<GQLUser> {
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
