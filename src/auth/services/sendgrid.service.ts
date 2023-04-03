import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as SendGrid from '@sendgrid/mail';

@Injectable()
export class SendgridService {
  constructor(private readonly configService: ConfigService) {
    SendGrid.setApiKey(this.configService.get<string>('SEND_GRID_KEY'));
  }

  // async send(mail: SendGrid.MailDataRequired) {
  //   const transport = await SendGrid.send(mail);
  //   return transport;
  // }

  async forgetPassword({
    username,
    email,
    token,
  }: {
    username: string;
    email: string;
    token: string;
  }) {
    // const mail = {
    //   to: email,
    //   subject: 'HiSudoku- reset password',
    //   from: {
    //     name: 'HiSudoku no-reply',
    //     email: this.configService.get<string>('SENDER_EMAIL'),
    //   },
    //   text: `Hi ${username}! Please use a given link to reset your password:
    //   ${this.configService.get<string>('CLIENT_URL')}/resetpassword,
    //   then use the following token: ${token}`,
    //   html: `
    //     <h1>
    //       Hi ${username}!
    //     </h1>
    //     <div>
    //       <p>Please click a given link to reset your password:</p>
    //       <p>
    //       ${this.configService.get<string>('CLIENT_URL')}/resetpassword
    //       </p>
    //     </div>
    //     <div>
    //       <p>then use the following token:</p>
    //       <p>${token}</p>
    //     </div>
    //   `,
    // };
    // return this.send(mail);
    console.log('username: %s, email: %s, token: %s', username, email, token);
  }

  async activateLink({
    username,
    email,
    token,
  }: {
    username: string;
    email: string;
    token: string;
  }) {
    // const mail = {
    //   to: email,
    //   subject: 'HiSudoku- activate email',
    //   from: {
    //     name: 'HiSudoku no-reply',
    //     email: this.configService.get<string>('SENDER_EMAIL'),
    //   },
    //   text: `Hi ${username}! Please use given link to activate your email:
    //   ${this.configService.get<string>('CLIENT_URL')}/activate-email/${token}`,
    //   html: `
    //   <h1>
    //     Hi ${username}!
    //   </h1>
    //   <div>
    //     <p>
    //     Please click a given link to activate your email:
    //     </p>
    //     <p>
    //     ${this.configService.get<string>('CLIENT_URL')}/activate-email/${token}
    //     </p>
    //   </div>
    //   `,
    // };
    // return this.send(mail);
    console.log('username: %s, email: %s, token: %s', username, email, token);
  }
}
