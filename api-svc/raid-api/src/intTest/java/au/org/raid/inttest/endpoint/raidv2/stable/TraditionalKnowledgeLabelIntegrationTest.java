package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.ValidationFailure;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TraditionalKnowledgeLabelIntegrationTest extends AbstractIntegrationTest {
    @Test
    @DisplayName("Minting a RAiD with a traditional knowledge label with null schemaUri fails")
    void nullSchemaUri() {
        createRequest.getTraditionalKnowledgeLabel().get(0).schemaUri(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("traditionalKnowledgeLabel[0].schemaUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a traditional knowledge label with empty schemaUri fails")
    void emptySchemaUri() {
        createRequest.getTraditionalKnowledgeLabel().get(0).schemaUri("");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("traditionalKnowledgeLabel[0].schemaUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a traditional knowledge label with invalid schemaUri fails")
    void invalidSchemaUri() {
        createRequest.getTraditionalKnowledgeLabel().get(0).schemaUri("http://localhost/");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("traditionalKnowledgeLabel[0].schemaUri")
                    .errorType("invalidValue")
                    .message("schema is unknown/unsupported")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a traditional knowledge label with invalid label id fails")
    void invalidLabelId() {
        createRequest.getTraditionalKnowledgeLabel().get(0).id("http://localhost/");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("traditionalKnowledgeLabel[0].id")
                    .errorType("invalidValue")
                    .message("id does not exist within the given schema")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("No validation failures if traditional knowledge labels are missing")
    void emptyTraditionalKnowledgeLabels() {
        createRequest.traditionalKnowledgeLabel(Collections.emptyList());

        try {
            raidApi.createRaidV1(createRequest);
            //pass
        } catch (RaidApiValidationException e) {
            fail("expected no failures");
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }
}
