package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.ContributorPosition;
import au.org.raid.idl.raidv2.model.ContributorRole;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ContributorsIntegrationTest extends AbstractIntegrationTest {
    @Test
    @DisplayName("Minting a RAiD with no contributor fails")
    void noContributors() {
        createRequest.setContributor(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with no contributors");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with empty contributor fails")
    void emptyContributors() {
        createRequest.setContributor(Collections.emptyList());

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with empty contributor");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing schemaUri fails")
    void missingIdentifierSchemeUri() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .position(List.of(
                                new ContributorPosition()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing schemaUri");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor[0].schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with empty schemaUri fails")
    void emptyIdentifierSchemeUri() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .schemaUri("")
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .position(List.of(
                                new ContributorPosition()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with empty schemaUri");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor[0].schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing contributor id fails")
    void missingId() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .position(List.of(
                                new ContributorPosition()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing contributor id");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor[0].id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with empty contributor id fails")
    void emptyId() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("")
                        .position(List.of(
                                new ContributorPosition()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with empty contributor id");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor[0].id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with null position fails")
    void nullPositions() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with null position");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor.position")
                            .errorType("invalidValue")
                            .message("leader must be specified")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with empty position fails")
    void emptyPositions() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .position(Collections.emptyList())
                        .role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with empty position");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor.position")
                            .errorType("invalidValue")
                            .message("leader must be specified")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing leader position fails")
    void missingLeader() {
        createRequest.setContributor(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .position(List.of(
                                new ContributorPosition()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(OTHER_PARTICIPANT_POSITION)
                        )).role(List.of(
                                new ContributorRole()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing leader position");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributor.position")
                            .errorType("invalidValue")
                            .message("leader must be specified")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Nested
    @DisplayName("Orcid tests...")
    class OrcidTests {
        @Test
        @DisplayName("Minting a RAiD with invalid orcid pattern fails")
        void invalidOrcidPattern() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0c00-0000-0000")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with invalid orcid pattern");
            } catch (RaidApiValidationException e) {
                assertThat(e.getFailures()).isEqualTo(List.of(
                        new ValidationFailure()
                                .fieldId("contributor[0].id")
                                .errorType("invalidValue")
                                .message("has invalid/unsupported value - should match ^https://orcid\\.org/[\\d]{4}-[\\d]{4}-[\\d]{4}-[\\d]{3}[\\d|X]{1}$")
                ));
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with invalid orcid checksum fails")
        void invalidOrcidChecksum() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0000")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with invalid orcid checksum");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].id")
                                .errorType("invalidValue")
                                .message("failed checksum, last digit should be `1`")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with non-existent orcid fails")
        void nonExistentOrcid() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0001-0000-0009")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with non-existent orcid");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].id")
                                .errorType("invalidValue")
                                .message("uri not found")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }
    }

    @Nested
    @DisplayName("Position tests...")
    class ContributorPositionTests {
        @Test
        @DisplayName("Minting a RAiD with missing position schemaUri fails")
        void missingPositionSchemeUri() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .id(LEADER_POSITION)
                            )).role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing contributor schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].position[0].schemaUri")
                                .errorType("notSet")
                                .message("field must be set")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with missing position type fails")
        void missingPositionType() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing contributor schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].position[1].id")
                                .errorType("notSet")
                                .message("field must be set")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with invalid position schemaUri fails")
        void invalidPositionSchemeUri() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri("https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v2")
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing contributor schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].position[1].schemaUri")
                                .errorType("invalidValue")
                                .message("schema is unknown/unsupported")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with invalid position type for schema fails")
        void invalidPositionTypeForScheme() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/unknown.json")
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing contributor schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].position[1].id")
                                .errorType("invalidValue")
                                .message("id does not exist within the given schema")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with a contributor with overlapping positions fails")
        void overlappingPositions() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .id(LEADER_POSITION)
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_LOCAL_DATE)),
                                    new ContributorPosition()
                                            .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/co-investigator.json")
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing contributor schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].position[1].startDate")
                                .errorType("invalidValue")
                                .message("Contributors can only hold one position at any given time. This position conflicts with contributor[0].position[0]")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("A raid can only have one leader at any given time")
        void multipleLeaders() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .id(LEADER_POSITION)
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_LOCAL_DATE))
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            )),
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .id(LEADER_POSITION)
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                                    ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))

            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing contributor schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[1].position[0]")
                                .errorType("invalidValue")
                                .message("There can only be one leader in any given period. The position at contributor[0].position[0] conflicts with this position.")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }
    }

    @Nested
    @DisplayName("Role tests...")
    class ContributorRoleTests {
        @Test
        @DisplayName("Minting a RAiD with missing role schemaUri fails")
        void missingRoleSchemeUri() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .id(LEADER_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
//              .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing role schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].role[0].schemaUri")
                                .errorType("notSet")
                                .message("field must be set")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with missing role type fails")
        void missingPositionType() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with missing role type");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].role[0].id")
                                .errorType("notSet")
                                .message("field must be set")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with invalid role schemaUri fails")
        void invalidPositionSchemeUri() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri("unknown")
                                            .id(SOFTWARE_CONTRIBUTOR_ROLE)
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with invalid role schemaUri");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].role[0].schemaUri")
                                .errorType("invalidValue")
                                .message("schema is unknown/unsupported")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }

        @Test
        @DisplayName("Minting a RAiD with invalid type for role schema fails")
        void invalidPositionTypeForScheme() {
            createRequest.setContributor(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .position(List.of(
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPosition()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .role(List.of(
                                    new ContributorRole()
                                            .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                            .id("unknown")
                            ))
            ));

            try {
                raidApi.createRaidV1(createRequest);
                fail("No exception thrown with invalid type for role schema");
            } catch (RaidApiValidationException e) {
                final var failures = e.getFailures();
                assertThat(failures).hasSize(1);
                assertThat(failures).contains(
                        new ValidationFailure()
                                .fieldId("contributor[0].role[0].id")
                                .errorType("invalidValue")
                                .message("id does not exist within the given schema")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }
    }
}