package au.org.raid.api.spring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "datacite")
public class DataCiteProperties {
    private String user;
    private String password;
    private String endpoint;
    private String prefix;



}
