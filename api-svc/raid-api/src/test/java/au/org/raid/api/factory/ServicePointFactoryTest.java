package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.ServicePoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class ServicePointFactoryTest {
    final ServicePointFactory factory = new ServicePointFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = 123L;
        final var name = "_name";
        final var adminEmail = "admin-email";
        final var techEmail = "tech-email";
        final var enabled = true;
        final var appWritesEnabled = true;

        final var servicePoint = new ServicePoint()
                .id(id)
                .name(name)
                .adminEmail(adminEmail)
                .techEmail(techEmail)
                .enabled(enabled)
                .appWritesEnabled(appWritesEnabled);

        final var result = factory.create(servicePoint);

        assertThat(result.getId(), is(id));
        assertThat(result.getName(), is(name));
        assertThat(result.getAdminEmail(), is(adminEmail));
        assertThat(result.getTechEmail(), is(techEmail));
        assertThat(result.getEnabled(), is(enabled));
        assertThat(result.getAppWritesEnabled(), is(appWritesEnabled));
    }
}