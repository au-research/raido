package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleTypeWithSchemaUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TitleIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Minting a RAiD with a title with an null language schemaUri fails")
    void nullLanguageSchemeUri() {
        createRequest.getTitles().get(0).getLanguage().schemaUri(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles[0].language.schemaUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a title with an empty language schemaUri fails")
    void emptyLanguageSchemeUri() {
        createRequest.getTitles().get(0).getLanguage().schemaUri("");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles[0].language.schemaUri")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a title with an empty language id fails")
    void emptyLanguageId() {
        createRequest.getTitles().get(0).getLanguage().setId("");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles[0].language.id")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a title with an null language fails")
    void nullLanguageId() {
        createRequest.getTitles().get(0).getLanguage().setId(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles[0].language.id")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a title with an invalid language id fails")
    void invalidLanguageId() {
        createRequest.getTitles().get(0).getLanguage().setId("xxx");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles[0].language.id")
                    .errorType("invalidValue")
                    .message("id does not exist within the given schema")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with a title with an invalid language schema fails")
    void invalidLanguageScheme() {
        createRequest.getTitles().get(0).getLanguage().schemaUri("http://localhost");

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles[0].language.schemaUri")
                    .errorType("invalidValue")
                    .message("schema is unknown/unsupported")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with null titles fails")
    void nullTitles() {
        createRequest.setTitles(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles")
                    .errorType("notSet")
                    .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing title fails")
    void missingTitle() {
        createRequest.setTitles(Collections.emptyList());

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(new ValidationFailure()
                    .fieldId("titles.type")
                    .errorType("missingPrimaryTitle")
                    .message("at least one primaryTitle entry must be provided")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with no primary title fails")
    void alternativeTitleOnly() {
        createRequest.getTitles().get(0).setType(new TitleTypeWithSchemaUri()
                .id(TestConstants.ALTERNATIVE_TITLE_TYPE)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing primary title");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("titles.type")
                            .errorType("missingPrimaryTitle")
                            .message("at least one primaryTitle entry must be provided")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing schemaUri fails")
    void missingTitleScheme() {
        createRequest.getTitles().get(0).setType(new TitleTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_TITLE_TYPE)
        );

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing schemaUri");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("titles[0].type.schemaUri")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with invalid schemaUri fails")
    void invalidTitleScheme() {
        createRequest.getTitles().get(0).setType(new TitleTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_TITLE_TYPE)
                .schemaUri("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v2"));

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with invalid schemaUri");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("titles[0].type.schemaUri")
                            .errorType("invalidValue")
                            .message("schema is unknown/unsupported")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing title type id fails")
    void missingTitleType() {
        final var titles = new ArrayList<>(createRequest.getTitles());

        titles.add(new Title()
                .title("Test Title")
                .type(new TitleTypeWithSchemaUri()
                        .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI)
                )
                .startDate(LocalDate.now())
        );

        createRequest.setTitles(titles);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing title type");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("titles[1].type.id")
                            .message("field must be set")
                            .errorType("notSet")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with invalid title type id fails")
    void invalidTitleType() {
        final var titles = new ArrayList<>(createRequest.getTitles());

        titles.add(new Title()
                .type(new TitleTypeWithSchemaUri()
                        .id("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/invalid.json")
                        .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI)
                )
                .title("Test Title")
                .startDate(LocalDate.now())
        );

        createRequest.setTitles(titles);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing invalid title type id");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);
            assertThat(failures).contains(
                    new ValidationFailure()
                            .message("id does not exist within the given schema")
                            .fieldId("titles[1].type.id")
                            .errorType("invalidValue")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }

    @Test
    @DisplayName("Minting a RAiD with missing startDate fails")
    void missingStartDate() {
        createRequest.getTitles().get(0).setStartDate(null);

        try {
            raidApi.createRaidV1(createRequest);
            fail("No exception thrown with missing startDate");
        } catch (RaidApiValidationException e) {
            final var failures = e.getFailures();
            assertThat(failures).hasSize(1);

            assertThat(failures).contains(
                    new ValidationFailure()
                            .fieldId("titles[0].startDate")
                            .errorType("notSet")
                            .message("field must be set")
            );
        } catch (Exception e) {
            fail("Expected RaidApiValidationException");
        }
    }
}