package au.org.raid.api.factory;

import au.org.raid.db.jooq.enums.IdProvider;
import au.org.raid.db.jooq.enums.UserRole;
import au.org.raid.db.jooq.tables.records.AppUserRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoFactoryTest {
    private final UserDtoFactory factory = new UserDtoFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = 123L;
        final var created = LocalDateTime.now();
        final var role = UserRole.SP_USER;
        final var clientId = "client-id";
        final var email = "_email";
        final var enabled = true;
        final var tokenExpiry = LocalDateTime.now().plusYears(1);
        final var servicePointId = 123L;
        final var idProvider = IdProvider.GOOGLE;

        final var record = new AppUserRecord()
                .setId(id)
                .setDateCreated(created)
                .setRole(role)
                .setClientId(clientId)
                .setEmail(email)
                .setEnabled(enabled)
                .setTokenCutoff(tokenExpiry)
                .setServicePointId(servicePointId)
                .setIdProvider(idProvider);

        final var result = factory.create(record);

        assertThat(result.getId(), is(id));
        assertThat(result.getCreated(), is(created.toLocalDate()));
        assertThat(result.getRole(), is("SP_USER"));
        assertThat(result.getClientId(), is(clientId));
        assertThat(result.getEmail(), is(email));
        assertThat(result.isEnabled(), is(enabled));
        assertThat(result.getTokenExpiry(), is(tokenExpiry.toLocalDate()));
        assertThat(result.getServicePointId(), is(servicePointId));
        assertThat(result.getIdProvider(), is("GOOGLE"));
    }
}