package au.org.raid.api.service.raid.validation;

import au.org.raid.idl.raidv2.model.SpatialCoverageBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

class SpatialCoverageValidatorTest {

    private final SpatialCoverageValidationService validator = new SpatialCoverageValidationService();

    @Test
    void noFailuresWhenSpatialCoverageIsNull() {
        final List<ValidationFailure> validationFailures = validator.validateSpatialCoverages(null);
        assertThat(validationFailures, is(empty()));
    }

    @Test
    void noFailuresWhenSpatialCoverageIsEmptyList() {
        final List<ValidationFailure> validationFailures = validator.validateSpatialCoverages(Collections.emptyList());
        assertThat(validationFailures, is(empty()));
    }

    @Test
    void noFailuresWhenSpatialCoverageIsValid() {
        final var spatialCoverage = "https://www.geonames.org/264371/athens.html";
        final var spatialCoverageSchemeUri = "https://www.geonames.org/";

        final var validationFailures = validator.validateSpatialCoverages(List.of(
                new SpatialCoverageBlock()
                        .spatialCoverage(spatialCoverage)
                        .spatialCoverageSchemeUri(spatialCoverageSchemeUri)
        ));

        assertThat(validationFailures, is(empty()));
    }

    @Test
    void addsFailureWhenSpatialCoverageIsNull() {
        final var spatialCoverageSchemeUri = "https://www.geonames.org/";

        final var validationFailures = validator.validateSpatialCoverages(List.of(
                new SpatialCoverageBlock()
                        .spatialCoverageSchemeUri(spatialCoverageSchemeUri)
        ));

        final var validationFailure = validationFailures.get(0);
        assertThat(validationFailures.size(), is(1));

        assertThat(validationFailure.getFieldId(), is("spatialCoverages[0].spatialCoverage"));
        assertThat(validationFailure.getErrorType(), is("required"));
        assertThat(validationFailure.getMessage(), is("This is a required field."));
    }

    @Test
    void addsFailureWhenSpatialCoverageIsInvalid() {
        final var spatialCoverage = "https://geonames.org/264371/athens.html";
        final var spatialCoverageSchemeUri = "https://www.geonames.org/";

        final var validationFailures = validator.validateSpatialCoverages(List.of(
                new SpatialCoverageBlock()
                        .spatialCoverage(spatialCoverage)
                        .spatialCoverageSchemeUri(spatialCoverageSchemeUri)
        ));

        final var validationFailure = validationFailures.get(0);
        assertThat(validationFailures.size(), is(1));

        assertThat(validationFailure.getFieldId(), is("spatialCoverages[0].spatialCoverage"));
        assertThat(validationFailure.getErrorType(), is("invalid"));
        assertThat(validationFailure.getMessage(), is("Uri is invalid."));
    }

    @Test
    void addsFailureWhenSpatialCoverageSchemeUriIsNull() {
        final var spatialCoverage = "https://www.geonames.org/264371/athens.html";

        final var validationFailures = validator.validateSpatialCoverages(List.of(
                new SpatialCoverageBlock()
                        .spatialCoverage(spatialCoverage)
        ));

        final var validationFailure = validationFailures.get(0);
        assertThat(validationFailures.size(), is(1));

        assertThat(validationFailure.getFieldId(), is("spatialCoverages[0].spatialCoverageSchemeUri"));
        assertThat(validationFailure.getErrorType(), is("required"));
        assertThat(validationFailure.getMessage(), is("This is a required field."));
    }

    @Test
    void addFailureWhenSpatialCoverageSchemeUriIsInvalid() {
        final var spatialCoverage = "https://www.geonames.org/264371/athens.html";
        final var spatialCoverageSchemeUri = "https://geonames.org/";

        final var validationFailures = validator.validateSpatialCoverages(List.of(
                new SpatialCoverageBlock()
                        .spatialCoverage(spatialCoverage)
                        .spatialCoverageSchemeUri(spatialCoverageSchemeUri)
        ));

        final var validationFailure = validationFailures.get(0);
        assertThat(validationFailures.size(), is(1));

        assertThat(validationFailure.getFieldId(), is("spatialCoverages[0].spatialCoverageSchemeUri"));
        assertThat(validationFailure.getErrorType(), is("invalid"));
        assertThat(validationFailure.getMessage(), is("Spatial coverage scheme uri should be https://www.geonames.org/."));
    }
}