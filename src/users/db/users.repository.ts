import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { FilterQuery, Model, Types } from 'mongoose';
import { UserDocument, User as DBUser } from './user.schema';
import { Role } from 'src/auth/roles';
import { User as GQLUser } from '../models/user.model';

@Injectable()
export class UsersRepository {
  constructor(
    @InjectModel(DBUser.name) private userModel: Model<UserDocument>,
  ) {}

  async createOne(user: DBUser): Promise<GQLUser> {
    const newUser = new this.userModel(user);
    await newUser.save();
    const userData: GQLUser = {
      id: newUser._id.toString(),
      username: newUser.username,
      email: newUser.email,
      roles: newUser.roles,
      createdAt: newUser.createdAt,
      updatedAt: newUser.updatedAt,
    };
    return userData;
  }

  async isUsernameExist(username: string) {
    return this.userModel.exists({ username });
  }

  async deleteById(userId: string | Types.ObjectId) {
    return this.userModel.deleteOne({ _id: userId });
  }

  async findPasswordByUsername(
    username: string,
  ): Promise<{ password: string }> {
    const user = await this.userModel.findOne({ username }).select('password');
    const privateData: { password: string } = { password: user.password };
    return privateData;
  }

  async findPasswordById(id: string): Promise<{ password: string }> {
    const user = await this.userModel.findById(id).select('password');
    const privateData: { password: string } = { password: user.password };
    return privateData;
  }

  async findMany({
    offset,
    perPage,
    usersFilterQuery = {},
  }: {
    usersFilterQuery: FilterQuery<DBUser>;
    offset: number;
    perPage: number;
  }): Promise<GQLUser[]> {
    const usersCollection = await this.userModel
      .find(usersFilterQuery)
      .select('_id username email roles createdAt updatedAt')
      .skip(offset)
      .limit(perPage)
      .sort({ createdAt: 'desc' });

    const filteredCollection: GQLUser[] = [];
    for (const user of usersCollection) {
      filteredCollection.push({
        id: user._id.toString(),
        username: user.username,
        email: user.email,
        roles: user.roles,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt,
      });
    }
    return filteredCollection;
  }

  async findOne(userFilterQuery: FilterQuery<DBUser>): Promise<GQLUser> {
    const user = await this.userModel
      .findOne(userFilterQuery)
      .select('_id username email roles createdAt updatedAt');
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOneById(id: string | Types.ObjectId): Promise<GQLUser> {
    const user = await this.userModel
      .findById(id)
      .select('_id username email roles createdAt updatedAt');
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findUsersByTheirIds({
    ids,
    offset,
    perPage,
  }: {
    ids: string[];
    offset: number;
    perPage: number;
  }): Promise<GQLUser[]> {
    const usersCollection = await this.userModel
      .find({ _id: { $in: ids } })
      .select('_id username email roles createdAt updatedAt')
      .skip(offset)
      .limit(perPage)
      .sort({ createdAt: 'desc' });
    const filteredCollection: GQLUser[] = [];
    for (const user of usersCollection) {
      filteredCollection.push({
        id: user._id.toString(),
        username: user.username,
        email: user.email,
        roles: user.roles,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt,
      });
    }
    return filteredCollection;
  }

  async findUserById(id: string | Types.ObjectId): Promise<GQLUser> {
    const user = await this.userModel
      .findById(id)
      .select('_id username email roles createdAt updatedAt');
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateResetLink({
    id,
    resetLink,
  }: {
    id: string | Types.ObjectId;
    resetLink: string;
  }): Promise<GQLUser> {
    const user = await this.userModel.findById(id);
    user.resetLink = resetLink;
    await user.save();
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateEmailAndRemoveResetLink({
    resetLink,
    email,
  }: {
    resetLink: string;
    email: string;
  }): Promise<GQLUser> {
    const user = await this.userModel.findOne({ resetLink });
    user.email = email;
    user.resetLink = '';
    await user.save();
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updatePasswordAndRemoveResetLink({
    resetLink,
    password,
  }: {
    resetLink: string;
    password: string;
  }): Promise<GQLUser> {
    const user = await this.userModel.findOne({ resetLink });
    user.password = password;
    user.resetLink = '';
    await user.save();
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateUsername({
    id,
    username,
  }: {
    id: string | Types.ObjectId;
    username: string;
  }): Promise<GQLUser> {
    const user = await this.userModel.findById(id);
    user.username = username;
    await user.save();
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updatePassword({
    id,
    password,
  }: {
    id: string | Types.ObjectId;
    password: string;
  }): Promise<GQLUser> {
    const user = await this.userModel.findById(id);
    user.password = password;
    await user.save();
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateRoles({
    id,
    roles,
  }: {
    id: string | Types.ObjectId;
    roles: Role[];
  }): Promise<GQLUser> {
    const user = await this.userModel.findById(id);
    user.roles = roles;
    await user.save();
    const userData: GQLUser = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async forceUpdate({
    user,
    userFilterQuery,
  }: {
    userFilterQuery: FilterQuery<UserDocument>;
    user: Partial<UserDocument>;
  }): Promise<GQLUser> {
    const newUser = await this.userModel.findOneAndUpdate(
      userFilterQuery,
      user,
      {
        new: true,
      },
    );
    const userData: GQLUser = {
      id: newUser._id.toString(),
      username: newUser.username,
      email: newUser.email,
      roles: newUser.roles,
      createdAt: newUser.createdAt,
      updatedAt: newUser.updatedAt,
    };
    return userData;
  }
}
