package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.ValidationFailure;
import raido.inttest.RaidApiValidationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static raido.inttest.endpoint.raidv2.stable.TestConstants.*;

public class AccessIntegrationTest extends AbstractStableIntegrationTest {
  @Test
  @DisplayName("Mint raid with valid open access type")
  void mintOpenAccess() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(OPEN_ACCESS_TYPE);

    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with valid closed access type")
  void mintClosedAccess() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(CLOSED_ACCESS_TYPE)
      .accessStatement("Closed");
    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with valid embargoed access type")
  void mintEmbargoedAccess() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(EMBARGOED_ACCESS_TYPE)
      .embargoExpiry(LocalDate.now());
    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with embargoed access type fails with missing embargoExpiry")
  void missingEmbargoExpiry() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(EMBARGOED_ACCESS_TYPE);
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
  @DisplayName("Mint with closed access type fails with missing accessStatement")
  void missingAccessStatement() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(CLOSED_ACCESS_TYPE);
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.accessStatement")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with closed access type fails with blank accessStatement")
  void blankAccessStatement() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(CLOSED_ACCESS_TYPE)
      .accessStatement("");

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.accessStatement")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with open access type fails with missing schemeUri")
  void missingSchemeUri() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .type(OPEN_ACCESS_TYPE);
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.schemeUri")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with open access type fails with blank schemeUri")
  void blankSchemeUri() {
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri("")
      .type(OPEN_ACCESS_TYPE);
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.schemeUri")
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
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI);
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.type")
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
    final var raidApi = basicRaidStableClient();
    final var createRequest = newCreateRequest();

    createRequest.getAccess()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type("");
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.type")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }
}