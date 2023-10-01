import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.useGlobalPipes(new ValidationPipe({ whitelist: true, transform: true }));

  const configService: ConfigService = app.get<ConfigService>(ConfigService);
  const port = configService.get('PORT');

  // If you're using Fastify as your server in NestJS instead of the default express server, you'll need to modify the server to listed on 0.0.0.0.
  await app.listen(port);
}
bootstrap();
