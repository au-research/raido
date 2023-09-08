package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemaUri;
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
    @DisplayName("Minting a RAiD with no contributors fails")
    void noContributors() {
        createRequest.setContributors(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with no contrbutors");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributors")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with empty contributors fails")
    void emptyContributors() {
        createRequest.setContributors(Collections.emptyList());

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with empty contributors");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributors")
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
        createRequest.setContributors(List.of(
                new Contributor()
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .positions(List.of(
                                new ContributorPositionWithSchemaUri()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .roles(List.of(
                                new ContributorRoleWithSchemaUri()
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
                            .fieldId("contributors[0].schemaUri")
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
        createRequest.setContributors(List.of(
                new Contributor()
                        .schemaUri("")
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .positions(List.of(
                                new ContributorPositionWithSchemaUri()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .roles(List.of(
                                new ContributorRoleWithSchemaUri()
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
                            .fieldId("contributors[0].schemaUri")
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
        createRequest.setContributors(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .positions(List.of(
                                new ContributorPositionWithSchemaUri()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .roles(List.of(
                                new ContributorRoleWithSchemaUri()
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
                            .fieldId("contributors[0].id")
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
        createRequest.setContributors(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("")
                        .positions(List.of(
                                new ContributorPositionWithSchemaUri()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(LEADER_POSITION)
                        ))
                        .roles(List.of(
                                new ContributorRoleWithSchemaUri()
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
                            .fieldId("contributors[0].id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with null positions fails")
    void nullPositions() {
        createRequest.setContributors(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .roles(List.of(
                                new ContributorRoleWithSchemaUri()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with null positions");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributors.positions")
                            .errorType("invalidValue")
                            .message("leader must be specified")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with empty positions fails")
    void emptyPositions() {
        createRequest.setContributors(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .positions(Collections.emptyList())
                        .roles(List.of(
                                new ContributorRoleWithSchemaUri()
                                        .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                        .id(SOFTWARE_CONTRIBUTOR_ROLE)
                        ))
        ));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with empty positions");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("contributors.positions")
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
        createRequest.setContributors(List.of(
                new Contributor()
                        .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                        .id("https://orcid.org/0000-0000-0000-0001")
                        .positions(List.of(
                                new ContributorPositionWithSchemaUri()
                                        .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                        .id(OTHER_PARTICIPANT_POSITION)
                        )).roles(List.of(
                                new ContributorRoleWithSchemaUri()
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
                            .fieldId("contributors.positions")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0c00-0000-0000")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].id")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0000")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].id")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0001-0000-0009")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].id")
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
    class ContributorPositionWithSchemaUriTests {
        @Test
        @DisplayName("Minting a RAiD with missing position schemaUri fails")
        void missingPositionSchemeUri() {
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .id(LEADER_POSITION)
                            )).roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].positions[0].schemaUri")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].positions[1].id")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri("https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v2")
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].positions[1].schemaUri")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/unknown.json")
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].positions[1].id")
                                .errorType("invalidValue")
                                .message("id does not exist within the given schema")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }
    }

    @Nested
    @DisplayName("Role tests...")
    class ContributorRoleWithSchemaUriTests {
        @Test
        @DisplayName("Minting a RAiD with missing role schemaUri fails")
        void missingRoleSchemeUri() {
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .id(LEADER_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].roles[0].schemaUri")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].roles[0].id")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].roles[0].schemaUri")
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
            createRequest.setContributors(List.of(
                    new Contributor()
                            .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .id("https://orcid.org/0000-0000-0000-0001")
                            .positions(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(LEADER_POSITION),
                                    new ContributorPositionWithSchemaUri()
                                            .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .id(OTHER_PARTICIPANT_POSITION)
                            ))
                            .roles(List.of(
                                    new ContributorRoleWithSchemaUri()
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
                                .fieldId("contributors[0].roles[0].id")
                                .errorType("invalidValue")
                                .message("id does not exist within the given schema")
                );
            } catch (Exception e) {
                fail("Expected RaidApiValidationException");
            }
        }
    }
}