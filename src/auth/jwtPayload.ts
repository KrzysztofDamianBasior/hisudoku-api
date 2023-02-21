import { Role } from './roles';

export type jwtPayload = {
  username: string;
  sub: string;
  roles: Role[];
};
