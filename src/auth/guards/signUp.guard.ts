import {
  Injectable,
  CanActivate,
  ExecutionContext,
  BadRequestException,
} from '@nestjs/common';
import { UsersService } from 'src/users/services/users.service';

@Injectable()
export class SignUpGuard implements CanActivate {
  constructor(private usersService: UsersService) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest();
    const newUser = request.body;

    if (newUser.username) {
      const user = await this.usersService.findByUsername(newUser.username);
      if (user) {
        throw new BadRequestException();
      }
      return true;
    }

    return false;
  }
}
