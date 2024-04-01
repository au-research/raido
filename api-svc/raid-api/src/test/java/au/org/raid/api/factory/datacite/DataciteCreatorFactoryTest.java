package au.org.raid.api.factory.datacite;

import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataciteCreatorFactoryTest {

    private final DataciteCreatorFactory dataciteCreatorFactory = new DataciteCreatorFactory();

    @Test
    public void testCreateWithNonNullRegistrationAgency() {
        RegistrationAgency registrationAgency = new RegistrationAgency();
        registrationAgency.setId("test-id");

//        DataciteCreator result = dataciteCreatorFactory.create(registrationAgency);

//        assertEquals("test-id", result.getNameType(), "DataciteCreator should be created with the correct ID");
    }

    @Test
    @DisplayName("Create with contributor - ORCID")
    void createWithContributorORCID() {
        final var id = "_id";
        final var schemaUri = "https://orcid.org/";

        final var contributor = new Contributor()
                .id(id)
                .schemaUri(schemaUri);

        final var result = dataciteCreatorFactory.create(contributor);

        assertThat(result.getName(), is(id));
        assertThat(result.getNameType(), is("Personal"));
        assertThat(result.getNameIdentifiers().get(0).getNameIdentifier(), is(id));
        assertThat(result.getNameIdentifiers().get(0).getNameIdentifierScheme(), is("ORCID"));
    }
    @Test
    @DisplayName("Create with contributor - ISNI")
    void createWithContributorISNI() {
        final var id = "_id";
        final var schemaUri = "https://isni.org/";

        final var contributor = new Contributor()
                .id(id)
                .schemaUri(schemaUri);

        final var result = dataciteCreatorFactory.create(contributor);

        assertThat(result.getName(), is(id));
        assertThat(result.getNameType(), is("Personal"));
        assertThat(result.getNameIdentifiers().get(0).getNameIdentifier(), is(id));
        assertThat(result.getNameIdentifiers().get(0).getNameIdentifierScheme(), is("ISNI"));
    }
}
