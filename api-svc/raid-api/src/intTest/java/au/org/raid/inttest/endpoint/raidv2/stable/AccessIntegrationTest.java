package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.AccessTypeWithSchemaUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class AccessIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Mint with invalid language id fails")
    void invalidLanguageId() {
        createRequest.getAccess().getStatement().getLanguage().setId("xxx");

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.statement.language.id")
                            .errorType("invalidValue")
                            .message("id does not exist within the given schema")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with invalid language schemaUri fails")
    void invalidLanguageSchemeUri() {
        createRequest.getAccess().getStatement().getLanguage().schemaUri("http://localhost");

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.statement.language.schemaUri")
                            .errorType("invalidValue")
                            .message("schema is unknown/unsupported")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with empty language schemaUri fails")
    void nullLanguageSchemeUri() {
        createRequest.getAccess().getStatement().getLanguage().schemaUri(null);

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.statement.language.schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with empty language schemaUri fails")
    void emptyLanguageSchemeUri() {
        createRequest.getAccess().getStatement().getLanguage().schemaUri("");

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.statement.language.schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with empty language id fails")
    void emptyLanguageId() {
        createRequest.getAccess().getStatement().getLanguage().setId("");

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.statement.language.id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with null language id fails")
    void nullLanguageId() {
        createRequest.getAccess().getStatement().getLanguage().setId(null);

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.statement.language.id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint raid with valid open access type")
    void mintOpenAccess() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id(OPEN_ACCESS_TYPE)
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI)
                );

        try {
            raidApi.createRaidV1(createRequest);
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }


    @Test
    @DisplayName("Mint with valid embargoed access type")
    void mintEmbargoedAccess() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id(EMBARGOED_ACCESS_TYPE)
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI)
                )
                .embargoExpiry(LocalDate.now())
                .statement(new AccessStatement().text("Embargoed"));
        try {
            raidApi.createRaidV1(createRequest);
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with embargoed access type fails with missing embargoExpiry")
    void missingEmbargoExpiry() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id(EMBARGOED_ACCESS_TYPE)
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI)
                )
                .statement(new AccessStatement().text("Embargoed"));
        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.embargoExpiry")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with closed access type fails")
    void blankAccessStatement() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id(CLOSED_ACCESS_TYPE)
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI)
                )
                .statement(new AccessStatement().text("Closed"));

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.type.id")
                            .errorType("invalidValue")
                            .message("Creating closed Raids is no longer supported")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with open access type fails with missing schemaUri")
    void missingSchemeUri() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id(OPEN_ACCESS_TYPE)
                );

        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.type.schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with open access type fails with blank schemaUri")
    void blankSchemeUri() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id(OPEN_ACCESS_TYPE)
                        .schemaUri("")
                );
        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.type.schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with open access type fails with missing type")
    void missingType() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI)
                );
        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.type.id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }

    @Test
    @DisplayName("Mint with open access type fails with blank type")
    void blankType() {
        createRequest.getAccess()
                .type(new AccessTypeWithSchemaUri()
                        .id("")
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI)
                );
        try {
            raidApi.createRaidV1(createRequest);
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("access.type.id")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Mint should be successful");
        }
    }
}