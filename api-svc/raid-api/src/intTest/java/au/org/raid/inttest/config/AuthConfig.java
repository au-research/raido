package au.org.raid.inttest.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "raid.test.auth", ignoreInvalidFields = true)
public class AuthConfig {
    private Client integrationTestClient;

    @PostConstruct
    private void validate() {
        if (integrationTestClient.clientId == null || integrationTestClient.clientSecret == null) {
            throw new IllegalStateException("raidPermissionsAdmin clientId/clientSecret not set");
        }
    }

    @Data
    public static class Client {
        private String clientId;
        private String clientSecret;
    }

}
