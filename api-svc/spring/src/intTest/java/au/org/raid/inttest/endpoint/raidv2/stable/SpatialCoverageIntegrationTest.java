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
        createRequest.getSpatialCoverages().get(0).setId("http://localhost");

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
        createRequest.getSpatialCoverages().get(0).setId(NONEXISTENT_TEST_GEONAMES_URI);

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
        createRequest.getSpatialCoverages().get(0).setId(SERVER_ERROR_TEST_GEONAMES_URI);

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
    @DisplayName("Minting a RAiD with a spatial coverage with a null language schemeUri fails")
    void nullLanguageSchemeUri() {
        createRequest.getSpatialCoverages().get(0).getLanguage().setSchemeUri(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.schemeUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a empty language schemeUri fails")
    void emptyLanguageSchemeUri() {
        createRequest.getSpatialCoverages().get(0).getLanguage().setSchemeUri("");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.schemeUri")
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
        createRequest.getSpatialCoverages().get(0).getLanguage().setId(null);

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
        createRequest.getSpatialCoverages().get(0).getLanguage().setId("");

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
        createRequest.getSpatialCoverages().get(0).getLanguage().setId("xxx");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.id")
                    .errorType("invalidValue")
                    .message("id does not exist within the given scheme")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }


    @Test
    @DisplayName("Minting a RAiD with a spatial coverage with a invalid language schemeUri fails")
    void invalidLanguageSchemeUri() {
        createRequest.getSpatialCoverages().get(0).getLanguage().setSchemeUri("xxx");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("spatialCoverages[0].language.schemeUri")
                    .errorType("invalidValue")
                    .message("scheme is unknown/unsupported")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

}
