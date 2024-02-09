package au.org.raid.api.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class EncryptionConverter implements Converter<String, String> {
    private final Key key;

    @Override
    @SneakyThrows
    public String from(final String databaseObject) {
        final var decodedBytes = Base64.getDecoder().decode(databaseObject);

        final var cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
    }

    @Override
    @SneakyThrows
    public String to(final String userObject) {
        final var cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(userObject.getBytes()));
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<String> toType() {
        return String.class;
    }
}
