import { Resolver, Query, Mutation, Args, Int } from '@nestjs/graphql';
import { UseGuards } from '@nestjs/common';
import { AuthService } from './services/auth.service';
import { SendgridService } from './services/sendgrid.service';
import { LocalAuthGuard } from './guards/local-auth.guard';
import { GqlAuthGuard } from './guards/gql-auth.guard';
import { SignUpInput } from './dto/signUp.input';
import { SignInInput } from './dto/signIn.input';
import { CurrentUser } from './current-user.decorator';
import { SignUpGuard } from './guards/signUp.guard';
import { User } from 'src/users/models/user.model';
import { ForgotPasswordInput } from './dto/forgotPassword.input';
import { ResetPasswordInput } from './dto/resetPasswordInput';
@Resolver()
export class AuthResolver {
  constructor(
    private readonly authService: AuthService,
    private readonly sendgridService: SendgridService,
  ) {}

  @UseGuards(SignUpGuard)
  @Mutation(() => String)
  async singUp(@Args('signUpInput') signUpInput: SignUpInput) {
    return this.authService.register({
      username: signUpInput.username,
      plainTextPassword: signUpInput.password,
      email: signUpInput.email,
    });
  }

  @UseGuards(LocalAuthGuard)
  @UseGuards(GqlAuthGuard)
  @Mutation(() => String)
  async singIn(
    @Args('signInInput') signInInput: SignInInput,
    @CurrentUser() user: User,
  ) {
    return this.authService.login({
      username: user.username,
      sub: user.id,
      roles: user.roles,
    });
  }

  @Mutation(() => String)
  async activateEmail(@Args('token', { type: () => String }) token: string) {
    return this.authService.activateEmail(token);
  }

  @Mutation(() => String)
  async forgotPassword(
    @Args('forgotPasswordInput') forgotPasswordInput: ForgotPasswordInput,
  ) {
    return this.authService.forgotPassword(forgotPasswordInput);
  }

  @Mutation(() => String)
  async resetPassword(
    @Args('resetPasswordInput') resetPasswordInput: ResetPasswordInput,
  ) {
    return this.authService.resetPassword(resetPasswordInput);
  }
}
