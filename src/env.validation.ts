import { plainToInstance } from 'class-transformer';
import { IsEnum, IsNumber, IsString, validateSync } from 'class-validator';

enum Environment {
  Development = 'development',
  Production = 'production',
  Test = 'test',
  Provision = 'provision',
}

export class EnvironmentVariables {
  @IsEnum(Environment)
  NODE_ENV: Environment;

  @IsNumber()
  PORT: number;

  @IsNumber()
  SALT_LENGTH: number;

  @IsString()
  JWT_SECRET: string;

  @IsString()
  MONGODB_URI: string;

  @IsString()
  MAILJET_PUBLIC_KEY: string;

  @IsString()
  MAILJET_SECRET_KEY: string;

  @IsString()
  ACTIVATE_EMAIL_URL: string;

  @IsString()
  RESET_PASSWORD_URL: string;
}

export function validate(config: Record<string, unknown>) {
  const validatedConfig = plainToInstance(EnvironmentVariables, config, {
    enableImplicitConversion: true,
  });
  const errors = validateSync(validatedConfig, {
    skipMissingProperties: false,
  });

  if (errors.length > 0) {
    throw new Error(errors.toString());
  }
  return validatedConfig;
}
