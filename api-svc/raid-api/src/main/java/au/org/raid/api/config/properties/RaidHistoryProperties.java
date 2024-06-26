package au.org.raid.api.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "raid.history")
@Data
public class RaidHistoryProperties {
    private int baselineInterval;
}
