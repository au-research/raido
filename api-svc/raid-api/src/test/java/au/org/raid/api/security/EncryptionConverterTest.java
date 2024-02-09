package au.org.raid.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EncryptionConverterTest {

    private EncryptionConverter converter;

    @BeforeEach
    void setUp() {
        final String keyValue = "pB7MB3iuYmmjWMTOCK6HS0JetzWhCtBtKoNl39pBr3Q=";

        byte[] decodedKey = Base64.getDecoder().decode(keyValue);

        converter = new EncryptionConverter(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
    }

    @Test
    @DisplayName("Encrypts and decrypts value")
    void name() {
        final String value = "Hello World!";

        final var encryptedValue = converter.to(value);

        assertThat(converter.from(encryptedValue), is(value));
    }
}