package au.org.raid.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "raid.raid-permissions")
public class RaidPermissionsAuthProps {
    private String clientId;
    private String clientSecret;
}
