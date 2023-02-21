import {
  BadRequestException,
  Inject,
  Injectable,
  forwardRef,
} from '@nestjs/common';
import { UsersService } from 'src/users/services/users.service';
import * as bcrypt from 'bcrypt';
import { SendgridService } from './sendgrid.service';
import { JwtService } from '@nestjs/jwt';
import { jwtPayload } from '../jwtPayload';
import { Role } from '../roles';

@Injectable()
export class AuthService {
  constructor(
    @Inject(forwardRef(() => UsersService))
    private usersService: UsersService,
    private sendgridService: SendgridService,
    private readonly jwtService: JwtService,
  ) {}

  async validateUser(username: string, password: string): Promise<any> {
    const userPassword = await this.usersService.findPasswordByUsername(
      username,
    );

    if (userPassword && bcrypt.compare(password, userPassword)) {
      return this.usersService.findByUsername(username);
    }
    return null;
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
    const user = await this.usersService.create({
      username,
      plainTextPassword,
    });
    if (email) {
      const resetLink = await this.sendActivateLink({
        id: user.id,
        username: user.username,
        email: email,
      });
      this.usersService.updateResetLink({ resetLink, id: user.id });
    }

    const payload: jwtPayload = {
      username: user.username,
      sub: user.id,
      roles: user.roles as unknown as Role[],
    };
    return {
      access_token: this.jwtService.sign(payload),
    };
  }

  async sendActivateLink({
    id,
    username,
    email,
  }: {
    id: string;
    username: string;
    email: string;
  }) {
    const activateEmailPayload = {
      email: email,
      sub: id,
    };
    const activateEmailToken = await this.jwtService.sign(activateEmailPayload);
    this.sendgridService.activateLink({
      username,
      email,
      token: activateEmailToken,
    });
    return activateEmailToken;
  }

  async verify(token: string) {
    const decoded = this.jwtService.verify(token);
    const user = this.usersService.findById(decoded.sub);
    if (!user) {
      throw new Error('Unable to get the user from decoded token.');
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

  async activateEmail(token: string) {
    //jwt = id + email
    const { email, sub }: { email: string; sub: string } =
      await this.jwtService.verify(token);
    if (email && sub) {
      return this.usersService.updateEmailAndRemoveResetLink({
        email,
        resetLink: token,
      });
    } else {
      throw new BadRequestException('Bad token');
    }
  }

  async forgotPassword({ email }: { email: string }) {
    const user = await this.usersService.findByEmail(email);
    const payload = {
      id: user.id,
      email: user.email,
      username: user.username,
    };
    const token = await this.jwtService.sign(payload);
    this.usersService.updateResetLink({ id: user.id, resetLink: token });
    return this.sendgridService.forgetPassword({
      username: payload.username,
      email: payload.email,
      token: token,
    });
  }

  async resetPassword({
    token,
    password,
  }: {
    token: string;
    password: string;
  }) {
    const { username, id }: { username: string; email: string; id: string } =
      await this.jwtService.verify(token);

    const user = await this.usersService.updatePasswordAndRemoveResetLink({
      password,
      resetLink: token,
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
