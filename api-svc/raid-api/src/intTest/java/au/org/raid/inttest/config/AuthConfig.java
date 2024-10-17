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
    private User admin;
    private User raidAu;
    private User uq;
    private User raidAdmin;
    private User raidUser;
    private Client integrationTestClient;


    @PostConstruct
    private void validate() {
        if (admin.user == null || admin.password == null) {
            throw new IllegalStateException("admin username/password not set");
        }
        if (raidAu.user == null || raidAu.password == null) {
            throw new IllegalStateException("raidAu username/password not set");
        }
        if (uq.user == null || uq.password == null) {
            throw new IllegalStateException("uq username/password not set");
        }
        if (raidAdmin.user == null || raidAdmin.password == null) {
            throw new IllegalStateException("raidAdmin username/password not set");
        }
        if (raidUser.user == null || raidUser.password == null) {
            throw new IllegalStateException("raidUser username/password not set");
        }
        if (integrationTestClient.clientId == null || integrationTestClient.clientSecret == null) {
            throw new IllegalStateException("raidPermissionsAdmin clientId/clientSecret not set");
        }
    }

    @Data
    public static class User {
        private String user;
        private String password;
    }

    @Data
    public static class Client {
        private String clientId;
        private String clientSecret;
    }

}
