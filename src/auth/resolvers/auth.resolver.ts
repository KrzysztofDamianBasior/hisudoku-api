import { Resolver, Mutation, Args, Context } from '@nestjs/graphql';

import { AuthService } from '../services/auth.service';

import { SignUpInput } from '../dto/signUp.input';
import { SignInInput } from '../dto/signIn.input';
import { ForgotPasswordInput } from '../dto/forgotPassword.input';
import { ResetPasswordInput } from '../dto/resetPasswordInput';

import { AuthResponse } from '../models/auth-response.model';
import { MyAccount } from 'src/users/models/myAccount.model';

@Resolver()
export class AuthResolver {
  constructor(private readonly authService: AuthService) {}

  @Mutation(() => AuthResponse)
  async singUp(@Args('signUpInput') signUpInput: SignUpInput) {
    return this.authService.register({
      username: signUpInput.username,
      plainTextPassword: signUpInput.password,
      email: signUpInput.email,
    });
  }

  @Mutation(() => AuthResponse)
  async singIn(
    @Args('signInInput') signInInput: SignInInput,
    @Context() context: any,
  ) {
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

  @Mutation(() => MyAccount)
  async activateEmail(@Args('token', { type: () => String }) token: string) {
    return this.authService.activateEmail({ token });
  }

  @Mutation(() => String)
  async forgotPassword(
    @Args('forgotPasswordInput') forgotPasswordInput: ForgotPasswordInput,
  ) {
    return this.authService.forgotPassword(forgotPasswordInput);
  }

  @Mutation(() => AuthResponse)
  async resetPassword(
    @Args('resetPasswordInput') resetPasswordInput: ResetPasswordInput,
  ) {
    return this.authService.resetPassword(resetPasswordInput);
  }
}
