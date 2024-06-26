package au.org.raid.api.factory.datacite;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import au.org.raid.idl.raidv2.model.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataciteAlternateIdentifierFactoryTest {

    private DataciteAlternateIdentifierFactory dataciteAlternateIdentifierFactory = new DataciteAlternateIdentifierFactory();
    @Test
    @DisplayName("Create set all fields")
    public void create() {
        final var id = "_id";

        final var alternateIdentifier = new AlternateIdentifier()
                .id(id);

        final var result = dataciteAlternateIdentifierFactory.create(alternateIdentifier);

        assertThat(result.getAlternateIdentifier(), is(id));
        assertThat(result.getAlternateIdentifierType(), is("URL"));
    }

    @Test
    @DisplayName("Create RaidAgencyUrl alternate id")
    public void createRaidAgencyUrl() {
        final var raidAgencyUrl = "raid-agency-url";

        final var id = new Id()
                .raidAgencyUrl(raidAgencyUrl);

        final var result = dataciteAlternateIdentifierFactory.create(id);

        assertThat(result.getAlternateIdentifier(), is(raidAgencyUrl));
        assertThat(result.getAlternateIdentifierType(), is("RaidAgencyUrl"));
    }
}
