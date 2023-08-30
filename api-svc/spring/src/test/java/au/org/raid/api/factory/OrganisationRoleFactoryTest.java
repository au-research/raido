package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.OrganisationRole;
import au.org.raid.idl.raidv2.model.OrganisationRoleSchemeType;
import au.org.raid.idl.raidv2.model.OrganisationRoleType;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class OrganisationRoleFactoryTest {
    private static final String SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1/";

    private final OrganisationRoleFactory roleFactory = new OrganisationRoleFactory();

    @Test
    @DisplayName("Contrator is set")
    void leaderSet() {
        final var role = new OrganisationRole()
                .role(OrganisationRoleType.CONTRACTOR)
                .roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

        final var expected = new OrganisationRoleWithSchemaUri()
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/contractor.json")
                .schemaUri(SCHEMA_URI);

        final var result = roleFactory.create(role);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Lead research organisation is set")
    void leadResearchOrganisationSet() {
        final var role = new OrganisationRole()
                .role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION)
                .roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

        final var expected = new OrganisationRoleWithSchemaUri()
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json")
                .schemaUri(SCHEMA_URI);

        final var result = roleFactory.create(role);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Other organisation is set")
    void otherOrganisationSet() {
        final var role = new OrganisationRole()
                .role(OrganisationRoleType.OTHER_ORGANISATION)
                .roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

        final var expected = new OrganisationRoleWithSchemaUri()
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/other-organisation.json")
                .schemaUri(SCHEMA_URI);

        final var result = roleFactory.create(role);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Other research organisation is set")
    void otherResearchOrganisationSet() {
        final var role = new OrganisationRole()
                .role(OrganisationRoleType.OTHER_RESEARCH_ORGANISATION)
                .roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

        final var expected = new OrganisationRoleWithSchemaUri()
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/other-research-organisation.json")
                .schemaUri(SCHEMA_URI);

        final var result = roleFactory.create(role);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Partner organisation is set")
    void partnerOrganisationSet() {
        final var role = new OrganisationRole()
                .role(OrganisationRoleType.PARTNER_ORGANISATION)
                .roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

        final var expected = new OrganisationRoleWithSchemaUri()
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/partner-organisation.json")
                .schemaUri(SCHEMA_URI);

        final var result = roleFactory.create(role);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Null OrganisationRole returns null")
    void returnsNull() {
        assertThat(roleFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("OrganisationRole with empty fields does not throw NullPointerException")
    void emptyFields() {
        assertThat(roleFactory.create(new OrganisationRole()), is(new OrganisationRoleWithSchemaUri()
                .id(null)
                .schemaUri(SCHEMA_URI)));
    }
}