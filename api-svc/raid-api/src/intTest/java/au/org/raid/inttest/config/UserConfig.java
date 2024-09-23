package au.org.raid.inttest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "raid.test.auth", ignoreInvalidFields = true)
public class UserConfig {
    private User admin;
    private User raidAu;
    private User uq;

    @Data
    public static class User {
        private String user;
        private String password;
    }

}
