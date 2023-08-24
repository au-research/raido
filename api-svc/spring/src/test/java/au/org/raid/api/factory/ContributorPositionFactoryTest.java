package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.ContributorPosition;
import au.org.raid.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType;
import au.org.raid.idl.raidv2.model.ContributorPositionSchemeType;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemeUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class ContributorPositionFactoryTest {
    private static final LocalDate START_DATE = LocalDate.now().minusYears(2);
    private static final LocalDate END_DATE = LocalDate.now().minusYears(1);

    private static final String SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/";

    private final ContributorPositionFactory positionFactory = new ContributorPositionFactory();

    @Test
    @DisplayName("Leader is set")
    void leaderSet() {
        final var position = new ContributorPosition()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .position(ContributorPositionRaidMetadataSchemaType.LEADER)
                .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);

        final var expected = new ContributorPositionWithSchemeUri()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json")
                .schemeUri(SCHEME_URI);

        final var result = positionFactory.create(position);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Co-investigator is set")
    void coInvestigatorSet() {
        final var position = new ContributorPosition()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .position(ContributorPositionRaidMetadataSchemaType.CO_INVESTIGATOR)
                .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);

        final var expected = new ContributorPositionWithSchemeUri()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/co-investigator.json")
                .schemeUri(SCHEME_URI);

        final var result = positionFactory.create(position);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Contact person is set")
    void contactPersonSet() {
        final var position = new ContributorPosition()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .position(ContributorPositionRaidMetadataSchemaType.CONTACT_PERSON)
                .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);

        final var expected = new ContributorPositionWithSchemeUri()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/contact-person.json")
                .schemeUri(SCHEME_URI);

        final var result = positionFactory.create(position);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Other participant is set")
    void otherParticipantSet() {
        final var position = new ContributorPosition()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .position(ContributorPositionRaidMetadataSchemaType.OTHER_PARTICIPANT)
                .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);

        final var expected = new ContributorPositionWithSchemeUri()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/other-participant.json")
                .schemeUri(SCHEME_URI);

        final var result = positionFactory.create(position);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Principal investigator is set")
    void principalInvestigatorSet() {
        final var position = new ContributorPosition()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .position(ContributorPositionRaidMetadataSchemaType.PRINCIPAL_INVESTIGATOR)
                .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);

        final var expected = new ContributorPositionWithSchemeUri()
                .startDate(START_DATE)
                .endDate(END_DATE)
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/principal-investigator.json")
                .schemeUri(SCHEME_URI);

        final var result = positionFactory.create(position);

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Null ContributorPosition returns null")
    void returnsNull() {
        assertThat(positionFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("ContributorPosition with empty fields does not throw NullPointerException")
    void emptyFields() {
        assertThat(positionFactory.create(new ContributorPosition()), is(new ContributorPositionWithSchemeUri()
                .startDate(null)
                .endDate(null)
                .id(null)
                .schemeUri(SCHEME_URI)));
    }
}