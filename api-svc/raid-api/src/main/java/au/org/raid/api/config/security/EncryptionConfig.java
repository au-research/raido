package au.org.raid.api.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Configuration
public class EncryptionConfig {
    @Value("${raid.db.encryption-key}")
    private String keyValue;

    @Bean
    public Key databaseEncryptionKey() {
        byte[] decodedKey = Base64.getDecoder().decode(keyValue);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
