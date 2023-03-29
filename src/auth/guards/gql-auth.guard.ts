import { ExecutionContext } from '@nestjs/common';
import { GqlExecutionContext } from '@nestjs/graphql';
import { AuthGuard } from '@nestjs/passport';

export class GqlAuthGuard extends AuthGuard('jwt') {
  getRequest(context: ExecutionContext): any {
    const ctx = GqlExecutionContext.create(context);
    return ctx.getContext().req;
  }
}

// This is what I'm using for GraphqlJwtAuthGuard based on documentaion:

// @Injectable()
// export class GqlJwtAuthGuard extends AuthGuard('jwt') {
//   constructor(private reflector: Reflector) {
//     super();
//   }

//   canActivate(ctx: ExecutionContext) {
//     const context = GqlExecutionContext.create(ctx);
//     const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
//       context.getHandler(),
//       context.getClass(),
//     ]);
//     if (isPublic) {
//       return true;
//     }
//     const { req } = context.getContext();
//     return super.canActivate(new ExecutionContextHost([req])); // NOTE
//   }
// }
