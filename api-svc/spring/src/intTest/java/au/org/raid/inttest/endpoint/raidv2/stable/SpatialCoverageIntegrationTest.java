package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.ValidationFailure;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static au.org.raid.api.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_GEONAMES_URI;
import static au.org.raid.api.service.stub.InMemoryStubTestData.SERVER_ERROR_TEST_GEONAMES_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class SpatialCoverageIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with an invalid uri fails")
    void invalidId() {
        createRequest.getSpatialCoverage().get(0).setId("http://localhost");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].id")
                    .errorType("invalidValue")
                    .message("has invalid/unsupported value - should match ^https://(www\\.)?geonames.org/\\d+/.*$")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with an non-existent uri fails")
    void nonExistentUri() {
        createRequest.getSpatialCoverage().get(0).setId(NONEXISTENT_TEST_GEONAMES_URI);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].id")
                    .errorType("invalidValue")
                    .message("uri not found")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Server error returns validation failure")
    void serverError() {
        createRequest.getSpatialCoverage().get(0).setId(SERVER_ERROR_TEST_GEONAMES_URI);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].id")
                    .errorType("invalidValue")
                    .message("uri could not be validated - server error")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a null language schemaUri fails")
    void nullLanguageSchemeUri() {
        createRequest.getSpatialCoverage().get(0).getLanguage().schemaUri(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.schemaUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a empty language schemaUri fails")
    void emptyLanguageSchemeUri() {
        createRequest.getSpatialCoverage().get(0).getLanguage().schemaUri("");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.schemaUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a null language id fails")
    void nullLanguageId() {
        createRequest.getSpatialCoverage().get(0).getLanguage().setId(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.id")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a empty language id fails")
    void emptyLanguageId() {
        createRequest.getSpatialCoverage().get(0).getLanguage().setId("");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.id")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a invalid language id fails")
    void invalidLanguageId() {
        createRequest.getSpatialCoverage().get(0).getLanguage().setId("xxx");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.id")
                    .errorType("invalidValue")
                    .message("id does not exist within the given schema")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }


    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a invalid language schemaUri fails")
    void invalidLanguageSchemeUri() {
        createRequest.getSpatialCoverage().get(0).getLanguage().schemaUri("xxx");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.schemaUri")
                    .errorType("invalidValue")
                    .message("schema is unknown/unsupported")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

}
