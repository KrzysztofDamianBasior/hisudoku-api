import { Resolver, Mutation, Args, Context } from '@nestjs/graphql';

import { AuthService } from '../services/auth.service';

import { SignUpInput } from '../dto/signUp.input';
import { SignInInput } from '../dto/signIn.input';
import { ForgotPasswordInput } from '../dto/forgotPassword.input';
import { ResetPasswordInput } from '../dto/resetPasswordInput';

import { AuthResponse } from '../models/auth-response.model';
import { MyAccount } from 'src/users/models/myAccount.model';
import { MessageResponse } from '../models/message-response.model';
import { ActivateEmailInput } from '../dto/activate-email.input';

@Resolver()
export class AuthResolver {
  constructor(private readonly authService: AuthService) {}

  @Mutation(() => AuthResponse, {
    description: 'A mutation that allows the user to create an account',
  })
  async singUp(@Args('signUpInput') signUpInput: SignUpInput) {
    return this.authService.register({
      username: signUpInput.username,
      plainTextPassword: signUpInput.password,
      email: signUpInput.email,
    });
  }

  @Mutation(() => AuthResponse, {
    description: 'A mutation that allows the user to authenticate himself',
  })
  async singIn(
    @Args('signInInput') signInInput: SignInInput,
    @Context() context: any,
  ): Promise<AuthResponse> {
    const user = await this.authService.validateUser({
      username: signInInput.username,
      password: signInInput.password,
    });
    return this.authService.login({
      username: user.username,
      sub: user.id,
      roles: user.roles,
    });
    // return this.authService.login({
    //   username: context.req.user.username,
    //   sub: context.req.user.id,
    //   roles: context.req.user.roles,
    // });
  }

  @Mutation(() => MyAccount, {
    name: 'activateEmail',
    description: "A mutation that activates the user's email",
  })
  async activateEmail(
    @Args('activateEmailInput') activateEmailInput: ActivateEmailInput,
  ): Promise<Omit<MyAccount, 'createdSudokus'>> {
    return this.authService.activateEmail({ token: activateEmailInput.token });
  }

  @Mutation(() => MessageResponse, {
    description:
      "A mutation that starts the user's password recovery procedure",
  })
  async forgotPassword(
    @Args('forgotPasswordInput') forgotPasswordInput: ForgotPasswordInput,
  ): Promise<MessageResponse> {
    return this.authService.forgotPassword(forgotPasswordInput);
  }

  @Mutation(() => AuthResponse, {
    description: "A mutation that resets the user's password",
  })
  async resetPassword(
    @Args('resetPasswordInput') resetPasswordInput: ResetPasswordInput,
  ): Promise<AuthResponse> {
    return this.authService.resetPassword({
      password: resetPasswordInput.newPassword,
      token: resetPasswordInput.token,
    });
  }
}
