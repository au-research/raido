package au.org.raid.api.validator;

import au.org.raid.api.util.SchemaUri;
import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.SpatialCoverage;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class SpatialCoverageValidatorTest {
    private LanguageValidator languageValidator;
    @BeforeEach
    void setUp() {
        languageValidator = mock(LanguageValidator.class);

    }

    @Test
    @DisplayName("Validation passes with valid spatial coverage")
    void validSpatialCoverage() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var spatialCoverage = new SpatialCoverage()
                .id("https://www.geonames.org/2643743/london.html")
                .schemaUri(SchemaUri.GEONAMES.getUri())
                .place("London")
                .language(language);

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, empty());
        verify(languageValidator).validate(language, "spatialCoverage[0]");
    }

    @Test
    @DisplayName("Adds language validation failures")
    void addLanguageValidationFailures() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var spatialCoverage = new SpatialCoverage()
                .id("https://www.geonames.org/2643743/london.html")
                .schemaUri(SchemaUri.GEONAMES.getUri())
                .place("London")
                .language(language);

        final var failure = new ValidationFailure()
                .fieldId("field-id")
                .errorType("error-type")
                .message("_message");

        when(languageValidator.validate(language, "spatialCoverage[0]")).thenReturn(List.of(failure));

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, is(List.of(failure)));
        verify(languageValidator).validate(language, "spatialCoverage[0]");
    }

    @Test
    @DisplayName("Adds uri validation failures")
    void addUriValidationFailures() {
        final var failure = new ValidationFailure()
                .fieldId("field-id")
                .errorType("error-type")
                .message("_message");

        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> List.of(failure)
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);
        final var uri = "https://www.geonames.org/2643743/london.html";
        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var spatialCoverage = new SpatialCoverage()
                .id(uri)
                .schemaUri("https://www.geonames.org/")
                .place("London")
                .language(language);

        when(languageValidator.validate(language, "spatialCoverage[0]"))
                .thenReturn(Collections.emptyList());

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, is(List.of(failure)));
        verify(languageValidator).validate(language, "spatialCoverage[0]");
    }

    @Test
    @DisplayName("Validation fails with null id")
    void nullId() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);
        final var spatialCoverage = new SpatialCoverage()
                .schemaUri("https://www.geonames.org/")
                .place("London");

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("spatialCoverage[0].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if id is empty string")
    void emptyId() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);

        final var spatialCoverage = new SpatialCoverage()
                .id("")
                .schemaUri("https://www.geonames.org/")
                .place("London");

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("spatialCoverage[0].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails with null schemaUri")
    void nullSchemeUri() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);

        final var spatialCoverage = new SpatialCoverage()
                .id("https://www.geonames.org/2643743/london.html")
                .place("London");

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("spatialCoverage[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails schemaUri is empty string")
    void emptySchemeUri() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);
        final var spatialCoverage = new SpatialCoverage()
                .id("https://www.geonames.org/2643743/london.html")
                .schemaUri("")
                .place("London");

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("spatialCoverage[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails with invalid schemaUri")
    void invalidSchemeUri() {
        final var uriValidatorMap = Map.of(
                SchemaUri.GEONAMES.getUri(), (BiFunction<String, String, List<ValidationFailure>>) (s, s2) -> Collections.emptyList()
        );

        final var validationService = new SpatialCoverageValidator(languageValidator, uriValidatorMap);
        final var spatialCoverage = new SpatialCoverage()
                .id("https://www.geonames.org/2643743/london.html")
                .schemaUri("https://wwwgeonames.org/")
                .place("London");

        final var failures = validationService.validate(List.of(spatialCoverage));
        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("spatialCoverage[0].schemaUri")
                        .errorType("invalidValue")
                        .message("schema is unknown/unsupported")
        ));
    }
}