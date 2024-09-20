package au.org.raid.api.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "raid.orcid-integration.raid-listener")
public class RaidListenerProperties {
    private String uri;
}
