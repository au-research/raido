import {Controller, Get} from '@nestjs/common';
import {HealthCheck} from "@nestjs/terminus";
import {Unprotected} from "nest-keycloak-connect";

@Controller('health')
export class HealthController {
    @Get()
    @HealthCheck()
    @Unprotected()
    check() {
        return {
            status: 'ok'
        }
    }
}
