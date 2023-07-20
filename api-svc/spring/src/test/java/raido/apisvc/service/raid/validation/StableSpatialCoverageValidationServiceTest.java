package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.SpatialCoverage;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class StableSpatialCoverageValidationServiceTest {
  private final StableSpatialCoverageValidationService validationService =
    new StableSpatialCoverageValidationService();

  @Test
  @DisplayName("Validation passes with valid spatial coverage")
  void validSpatialCoverage() {
    final var spatialCoverage = new SpatialCoverage()
      .id("https://www.geonames.org/2643743/london.html")
      .schemeUri("https://www.geonames.org/")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Validation fails with null id")
  void nullId() {
    final var spatialCoverage = new SpatialCoverage()
      .schemeUri("https://www.geonames.org/")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("spatialCoverages[0].id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails if id is empty string")
  void emptyId() {
    final var spatialCoverage = new SpatialCoverage()
      .id("")
      .schemeUri("https://www.geonames.org/")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("spatialCoverages[0].id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with invalid id pattern")
  void invalidIdPattern() {
    final var spatialCoverage = new SpatialCoverage()
      .id("https://www.geonames.org/2643743/london")
      .schemeUri("https://www.geonames.org/")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("spatialCoverages[0].id")
        .errorType("invalidValue")
        .message("has invalid/unsupported value")
    ));
  }

  @Test
  @DisplayName("Validation fails with null schemeUri")
  void nullSchemeUri() {
    final var spatialCoverage = new SpatialCoverage()
      .id("https://www.geonames.org/2643743/london.html")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("spatialCoverages[0].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails schemeUri is empty string")
  void emptySchemeUri() {
    final var spatialCoverage = new SpatialCoverage()
      .id("https://www.geonames.org/2643743/london.html")
      .schemeUri("")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("spatialCoverages[0].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with invalid schemeUri")
  void invalidSchemeUri() {
    final var spatialCoverage = new SpatialCoverage()
      .id("https://www.geonames.org/2643743/london.html")
      .schemeUri("https://wwwgeonames.org/")
      .place("London");

    final var failures = validationService.validate(List.of(spatialCoverage));
    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("spatialCoverages[0].schemeUri")
        .errorType("invalidValue")
        .message("Spatial coverage scheme uri should be https://www.geonames.org/")
    ));
  }
}