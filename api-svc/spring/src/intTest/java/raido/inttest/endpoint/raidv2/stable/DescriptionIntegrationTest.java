package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.inttest.RaidApiValidationException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class DescriptionIntegrationTest extends AbstractStableIntegrationTest {
  @Test
  @DisplayName("Minting a RAiD with missing description block succeeds")
  void missingTitle() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.setDescriptions(Collections.emptyList());

    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Description block should be optional");
    }
  }

  @Test
  @DisplayName("Validation fails with missing schemeUri")
  void missingSchemeUri() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0).schemeUri(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].schemeUri");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with invalid schemeUri")
  void invalidSchemeUri() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0)
      .schemeUri("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v2");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with invalid schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].schemeUri");
      assertThat(failures.get(0).getErrorType()).isEqualTo("invalidValue");
      assertThat(failures.get(0).getMessage()).isEqualTo("has invalid/unsupported value");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with blank schemeUri")
  void blankSchemeUri() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0).schemeUri("");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].schemeUri");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with missing description")
  void missingDescription() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0).description(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].description");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with blank description")
  void blankDescription() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0).description("");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].description");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with missing type")
  void missingType() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0).type(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].type");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails with blank type")
  void blankType() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0).type("");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].type");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Validation fails if type is not found within scheme")
  void invalidType() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getDescriptions().get(0)
      .type("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/unknown.json");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("descriptions[0].type");
      assertThat(failures.get(0).getErrorType()).isEqualTo("invalidValue");
      assertThat(failures.get(0).getMessage()).isEqualTo("has invalid/unsupported value");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

}