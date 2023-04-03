import {
  BadRequestException,
  Inject,
  Injectable,
  UnauthorizedException,
  forwardRef,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';

import {
  MyAccountWithoutNestedFields,
  UsersService,
} from 'src/users/services/users.service';
import { SendgridService } from './sendgrid.service';

import { jwtPayload } from '../jwtPayload';
import { MyAccount } from 'src/users/models/myAccount.model';

import { User as GQLUser } from 'src/users/models/user.model';
import { MessageResponse } from '../models/message-response.model';

@Injectable()
export class AuthService {
  constructor(
    @Inject(forwardRef(() => UsersService))
    private usersService: UsersService,
    private sendgridService: SendgridService,
    private readonly jwtService: JwtService,
  ) {}

  async validateUser({
    username,
    password,
  }: {
    username: string;
    password: string;
  }): Promise<GQLUser> {
    const userPrivateDetails = await this.usersService.findPasswordByUsername({
      username,
    });
    const userPassword = userPrivateDetails.password;

    if (userPassword) {
      const passwordComparison: boolean = await bcrypt.compare(
        password,
        userPassword,
      );
      if (passwordComparison) {
        const user: GQLUser = await this.usersService.findOneByUsername({
          username,
        });
        if (!user) {
          throw new UnauthorizedException();
        }
        return user;
      } else {
        throw new UnauthorizedException();
      }
    } else {
      throw new UnauthorizedException();
    }
  }

  async register({
    username,
    plainTextPassword,
    email,
  }: {
    username: string;
    plainTextPassword: string;
    email?: string;
  }): Promise<{ access_token: string }> {
    const user: MyAccountWithoutNestedFields =
      await this.usersService.createOne({
        username,
        plainTextPassword,
        email,
        roles: ['User'],
      });
    const payload: jwtPayload = {
      name: user.username,
      sub: user.id,
      iss: 'HiSudoku',
      roles: user.roles,
    };
    return {
      access_token: this.jwtService.sign(payload),
    };
  }

  async sendActivateEmailLink({
    id,
    username,
    email,
  }: {
    id: string;
    username: string;
    email: string;
  }): Promise<string> {
    const activateEmailPayload = {
      email: email,
      sub: id,
    };
    const activateEmailToken = await this.jwtService.sign(activateEmailPayload);
    await this.sendgridService.activateLink({
      username,
      email,
      token: activateEmailToken,
    });
    return activateEmailToken;
  }

  async verify({ token }: { token: string }) {
    const decoded = await this.jwtService.verify(token);
    const user = await this.usersService.findOneById({ userId: decoded.sub });
    if (!user) {
      throw new BadRequestException(
        'Unable to get the user from decoded token.',
      );
    }
    return user;
  }

  async login({
    username,
    sub,
    roles,
  }: {
    username: string;
    sub: string;
    roles: string[];
  }): Promise<{ access_token: string }> {
    const payload = {
      username,
      sub,
      roles,
    };
    return {
      access_token: this.jwtService.sign(payload),
    };
  }

  async activateEmail({
    token,
  }: {
    token: string;
  }): Promise<Omit<MyAccount, 'createdSudokus'>> {
    //jwt = id + email
    const { email, sub }: { email: string; sub: string } =
      await this.jwtService.verify(token);
    if (email && sub) {
      return this.usersService.activateEmail({
        userId: sub,
        newEmail: email,
      });
    } else {
      throw new BadRequestException('Bad token');
    }
  }

  async forgotPassword({ email }: { email: string }): Promise<MessageResponse> {
    const user = await this.usersService.findMyAccountByEmail({ email });
    const payload = {
      id: user.id,
      email: user.email,
      username: user.username,
    };
    const token = await this.jwtService.sign(payload);
    await this.usersService.updateResetPasswordLink({
      userId: user.id,
      resetPasswordLink: token,
    });
    await this.sendgridService.forgetPassword({
      username: payload.username,
      email: payload.email,
      token: token,
    });
    const response: MessageResponse = {
      message: 'Email was sent',
    };
    return response;
  }

  async resetPassword({
    token,
    password,
  }: {
    token: string;
    password: string;
  }): Promise<{ access_token: string }> {
    const { username, id }: { username: string; email: string; id: string } =
      await this.jwtService.verify(token);

    const user =
      await this.usersService.updatePasswordAndRemoveResetPasswordLink({
        password,
        resetPasswordLink: token,
      });
    const payload = {
      username,
      sub: id,
      roles: user.roles,
    };
    return {
      access_token: this.jwtService.sign(payload),
    };
  }
}
