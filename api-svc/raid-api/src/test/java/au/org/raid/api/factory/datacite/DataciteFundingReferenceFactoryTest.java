package au.org.raid.api.factory.datacite;

import au.org.raid.idl.raidv2.model.Organisation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataciteFundingReferenceFactoryTest {
    private DataciteFundingReferenceFactory fundingReferenceFactory = new DataciteFundingReferenceFactory();

    @Test
    @DisplayName("Create sets all fields")
    void setAllFields() {
        final var id = "_id";
        final var schemaUri = "schema-uri";

        final var organisation = new Organisation()
                .id(id)
                .schemaUri(schemaUri);

        final var result = fundingReferenceFactory.create(organisation);

        assertThat(result.getFunderName(), is(id));
        assertThat(result.getFunderIdentifier(), is(id));
        assertThat(result.getFunderIdentifierType(), is("ROR"));
        assertThat(result.getSchemeUri(), is(schemaUri));
    }
}