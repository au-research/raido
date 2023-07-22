package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.ValidationFailure;
import raido.inttest.RaidApiValidationException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static raido.inttest.endpoint.raidv2.stable.TestConstants.DESCRIPTION_TYPE_SCHEME_URI;

public class DescriptionIntegrationTest extends AbstractStableIntegrationTest {
  @Test
  @DisplayName("Minting a RAiD with missing description block succeeds")
  void missingTitle() {
    createRequest.setDescriptions(Collections.emptyList());

    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Description should be optional");
    }
  }

  @Test
  @DisplayName("Validation fails with missing schemeUri")
  void missingSchemeUri() {
    createRequest.getDescriptions().get(0).getType().schemeUri(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("descriptions[0].type.schemeUri")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with invalid schemeUri")
  void invalidSchemeUri() {
    createRequest.getDescriptions().get(0).getType()
      .schemeUri("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v2");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with invalid schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].type.schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with blank schemeUri")
  void blankSchemeUri() {
    createRequest.getDescriptions().get(0).getType().schemeUri("");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with blank schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("descriptions[0].type.schemeUri")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with missing description")
  void missingDescription() {
    createRequest.getDescriptions().get(0).description(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing description");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].description")
        .errorType("notSet")
        .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with blank description")
  void blankDescription() {
    createRequest.getDescriptions().get(0).description("");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with blank description");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].description")
        .errorType("notSet")
        .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with missing type")
  void nullType() {
    createRequest.getDescriptions().get(0).type(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing type");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].type")
        .errorType("notSet")
        .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with missing type id")
  void missingId() {
    createRequest.getDescriptions().get(0).type(
      new DescriptionType()
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
    );

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing type id");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].type.id")
        .errorType("notSet")
        .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with empty id")
  void emptyId() {
    createRequest.getDescriptions().get(0).type(
      new DescriptionType()
        .id("")
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
    );

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing description type id");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].type.id")
        .errorType("notSet")
        .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with blank type")
  void blankType() {
    createRequest.getDescriptions().get(0).getType().id("");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].type.id")
        .errorType("notSet")
        .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails if type is not found within scheme")
  void invalidType() {
    createRequest.getDescriptions().get(0)
      .getType().id("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/unknown.json");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown when id not found in scheme");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(new ValidationFailure()
        .fieldId("descriptions[0].type.id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }
}