package au.org.raid.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication
public class Api {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
    public static void main(String[] args) {
        SpringApplication.run(Api.class, args);
    }
}