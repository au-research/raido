import {Module} from '@nestjs/common';
import {AppController} from './app.controller';
import {AppService} from './app.service';
import {HealthModule} from './health/health.module';
import {AuthGuard, KeycloakConnectModule} from "nest-keycloak-connect";
import {APP_GUARD} from "@nestjs/core";
import {keycloakConfig} from "../auth/KeycloakConfig";
import {ConfigModule} from "@nestjs/config";
import * as dotenv from 'dotenv';

dotenv.config();
@Module({
  imports: [
    HealthModule,
    KeycloakConnectModule.register({
      authServerUrl: keycloakConfig.authServerUrl(),
      realm: keycloakConfig.realm(),
      clientId: keycloakConfig.clientId(),
      secret: keycloakConfig.secret(),
    }),
    ConfigModule.forRoot()
  ],
  controllers: [AppController],
  providers: [
      AppService,
    {
      provide: APP_GUARD,
      useClass: AuthGuard
    }
  ],
})
export class AppModule {}
