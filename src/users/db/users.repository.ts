import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { FilterQuery, Model, Types } from 'mongoose';

import { UserDocument, User as DBUser } from './user.schema';

import { Role } from 'src/auth/roles';

export type PublicUserDetails = {
  id: string;
  username: string;
  roles: string[];
  createdAt: Date;
  updatedAt: Date;
};

export type ProtectedUserDetails = {
  email: string;
};

export type OpenUserDetails = PublicUserDetails & ProtectedUserDetails;

export type PrivateUserDetails = {
  password: string;
  resetPasswordLink: string;
};

export type UserFeed = {
  users: PublicUserDetails[];
  hasNextPage: boolean;
  cursor: string;
};

@Injectable()
export class UsersRepository {
  constructor(
    @InjectModel(DBUser.name) private userModel: Model<UserDocument>,
  ) {}

  async createOne({
    username,
    hashedPassword,
    roles,
    email,
  }: {
    username: string;
    hashedPassword: string;
    roles: string[];
    email?: string;
  }): Promise<OpenUserDetails> {
    const user: DBUser = {
      username,
      roles,
      email: email,
      password: hashedPassword,
      resetPasswordLink: '',
    };
    const newUser = new this.userModel(user);
    await newUser.save();
    const userData: OpenUserDetails = {
      id: newUser._id.toString(),
      username: newUser.username,
      email: newUser.email,
      roles: newUser.roles,
      createdAt: newUser.createdAt,
      updatedAt: newUser.updatedAt,
    };
    return userData;
  }

  async doesUsernameExist({ username }: { username: string }) {
    return this.userModel.exists({ username });
  }

  async doesEmailExist({ email }: { email: string }) {
    return this.userModel.exists({ email });
  }

  async removeOneById({
    userId,
  }: {
    userId: string | Types.ObjectId;
  }): Promise<OpenUserDetails | null> {
    const user = await this.userModel.findOneAndDelete({ _id: userId });
    if (!user) {
      return null;
    }
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOnePrivateDetailsByUsername({
    username,
  }: {
    username: string;
  }): Promise<PrivateUserDetails | null> {
    const user = await this.userModel.findOne({ username });

    if (!user) {
      return null;
    }

    const privateData: PrivateUserDetails = {
      password: user.password,
      resetPasswordLink: user.resetPasswordLink,
    };
    return privateData;
  }

  async findOnePrivateDetailsById({
    userId,
  }: {
    userId: string;
  }): Promise<PrivateUserDetails | null> {
    const user = await this.userModel.findById(userId);

    if (!user) {
      return null;
    }

    const privateData: PrivateUserDetails = {
      password: user.password,
      resetPasswordLink: user.resetPasswordLink,
    };
    return privateData;
  }

  async findMany({
    usersFilterQuery = {},
    offset,
    perPage,
  }: {
    usersFilterQuery: FilterQuery<DBUser>;
    offset: number;
    perPage: number;
  }): Promise<PublicUserDetails[]> {
    const usersCollection = await this.userModel
      .find(usersFilterQuery)
      .skip(offset)
      .limit(perPage)
      .sort({ createdAt: 'desc' });

    const filteredCollection: PublicUserDetails[] = [];
    for (const user of usersCollection) {
      filteredCollection.push({
        id: user._id.toString(),
        username: user.username,
        roles: user.roles,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt,
      });
    }
    return filteredCollection;
  }

  async userFeed({
    cursor,
    limit,
  }: {
    cursor: string | null;
    limit: number;
  }): Promise<UserFeed> {
    //if no cursor has been passed, the default query will be empty, it will retrieve the latest users from the database
    let cursorQuery = {};

    //if a cursor has been passed, the query will look for users whose ObjectId value is less than the cursor value
    if (cursor) {
      cursorQuery = { _id: { $lt: cursor } };
    }

    let usersCollection = await this.userModel
      .find(cursorQuery)
      .sort({ _id: -1 })
      .limit(limit + 1);

    let hasNextPage = false;
    if (usersCollection.length > limit) {
      hasNextPage = true;
      usersCollection = usersCollection.slice(0, -1);
    }

    if (usersCollection.length > 0) {
      //the cursor is the mongo identifier of the last element in the array
      const newCursor =
        usersCollection[usersCollection.length - 1]._id.toString();

      const filteredCollection: PublicUserDetails[] = [];
      for (const user of usersCollection) {
        filteredCollection.push({
          id: user._id.toString(),
          username: user.username,
          roles: user.roles,
          createdAt: user.createdAt,
          updatedAt: user.updatedAt,
        });
      }
      return { cursor: newCursor, hasNextPage, users: filteredCollection };
    } else {
      return { cursor: null, hasNextPage, users: [] };
    }
  }

  async findOnePublicDetails({
    userFilterQuery,
  }: {
    userFilterQuery: FilterQuery<PublicUserDetails>;
  }): Promise<PublicUserDetails | null> {
    const user = await this.userModel.findOne(userFilterQuery);

    if (!user) {
      return null;
    }

    const userData: PublicUserDetails = {
      id: user._id.toString(),
      username: user.username,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOnePublicDetailsById({
    userId,
  }: {
    userId: string | Types.ObjectId;
  }): Promise<PublicUserDetails | null> {
    const user = await this.userModel.findById(userId);

    if (!user) {
      return null;
    }

    const userData: PublicUserDetails = {
      id: user._id.toString(),
      username: user.username,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOneOpenDetailsById({
    userId,
  }: {
    userId: string | Types.ObjectId;
  }): Promise<OpenUserDetails | null> {
    const user = await this.userModel.findById(userId);
    if (!user) {
      return null;
    }
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOneOpenDetailsByEmail({
    email,
  }: {
    email: string | Types.ObjectId;
  }): Promise<OpenUserDetails | null> {
    const user = await this.userModel.findOne({ email });
    if (!user) {
      return null;
    }
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOnePublicDetailsByUsername({
    username,
  }: {
    username: string | Types.ObjectId;
  }): Promise<PublicUserDetails | null> {
    const user = await this.userModel.findOne({ username });
    if (!user) {
      return null;
    }
    const userData: PublicUserDetails = {
      id: user._id.toString(),
      username: user.username,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async findOnePublicDetailsByResetPasswordLink({
    resetPasswordLink,
  }: {
    resetPasswordLink: string | Types.ObjectId;
  }): Promise<PublicUserDetails | null> {
    const user = await this.userModel.findOne({ resetPasswordLink });
    if (!user) {
      return null;
    }
    const userData: PublicUserDetails = {
      id: user._id.toString(),
      username: user.username,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async userFeedByTheirIds({
    ids,
    cursor,
    limit,
  }: {
    ids: string[];
    cursor: string | null;
    limit: number;
  }): Promise<UserFeed> {
    let cursorQuery: any = { _id: { $in: ids } };

    //if a cursor has been passed, the query will look for users whose ObjectId value is less than the cursor value
    if (cursor) {
      cursorQuery = { $and: [{ $lt: cursor }, { _id: { $in: ids } }] };
    }

    let usersCollection = await this.userModel
      .find(cursorQuery)
      .sort({ _id: -1 })
      .limit(limit + 1);

    let hasNextPage = false;
    if (usersCollection.length > limit) {
      hasNextPage = true;
      usersCollection = usersCollection.slice(0, -1);
    }

    if (usersCollection.length > 0) {
      //the cursor is the mongo identifier of the last element in the array
      const newCursor =
        usersCollection[usersCollection.length - 1]._id.toString();

      const filteredCollection: PublicUserDetails[] = [];
      for (const user of usersCollection) {
        filteredCollection.push({
          id: user._id.toString(),
          username: user.username,
          roles: user.roles,
          createdAt: user.createdAt,
          updatedAt: user.updatedAt,
        });
      }
      return { cursor: newCursor, hasNextPage, users: filteredCollection };
    } else {
      return { cursor: null, hasNextPage, users: [] };
    }
  }

  async updateResetPasswordLink({
    userId,
    newResetPasswordLink,
  }: {
    userId: string | Types.ObjectId;
    newResetPasswordLink: string;
  }): Promise<PublicUserDetails | null> {
    const user = await this.userModel.findById(userId);
    if (!user) {
      return null;
    }
    user.resetPasswordLink = newResetPasswordLink;
    await user.save();
    const userData: PublicUserDetails = {
      id: user._id.toString(),
      username: user.username,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateOnePasswordAndRemoveResetPasswordLink({
    resetPasswordLink,
    newPassword,
  }: {
    resetPasswordLink: string;
    newPassword: string;
  }): Promise<OpenUserDetails | null> {
    const user = await this.userModel.findOne({ resetPasswordLink });
    if (!user) {
      return null;
    }
    user.password = newPassword;
    user.resetPasswordLink = '';
    await user.save();
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateOneUsername({
    userId,
    newUsername,
  }: {
    userId: string | Types.ObjectId;
    newUsername: string;
  }): Promise<OpenUserDetails | null> {
    const user = await this.userModel.findById(userId);
    if (!user) {
      return null;
    }
    user.username = newUsername;
    await user.save();
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateOneEmail({
    userId,
    newEmail,
  }: {
    userId: string | Types.ObjectId;
    newEmail: string;
  }): Promise<OpenUserDetails> {
    const user = await this.userModel.findById(userId);
    if (!user) {
      return null;
    }
    user.email = newEmail;
    await user.save();
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateOnePassword({
    userId,
    newPassword,
  }: {
    userId: string | Types.ObjectId;
    newPassword: string;
  }): Promise<OpenUserDetails> {
    const user = await this.userModel.findById(userId);
    if (!user) {
      return null;
    }
    user.password = newPassword;
    await user.save();
    const userData: OpenUserDetails = {
      id: user._id.toString(),
      username: user.username,
      email: user.email,
      roles: user.roles,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };
    return userData;
  }

  async updateOneRoles({
    userId,
    newRoles,
  }: {
    userId: string | Types.ObjectId;
    newRoles: Role[];
  }): Promise<PublicUserDetails> {
    const user = await this.userModel.findById(userId);
    if (!user) {
      return null;
    }
    user.roles = newRoles;
    await user.save();
    const userData: PublicUserDetails = {
      id: user._id.toString(),
      username: user.username,
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
  }): Promise<OpenUserDetails> {
    const newUser = await this.userModel.findOneAndUpdate(
      userFilterQuery,
      user,
      {
        new: true,
      },
    );
    if (!newUser) {
      return null;
    }
    const userData: OpenUserDetails = {
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
