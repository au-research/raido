package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.AccessType;
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

    createRequest.getAccess()
      .type(new AccessType()
        .id(OPEN_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      );

    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with valid closed access type")
  void mintClosedAccess() {
    createRequest.getAccess()
      .type(new AccessType()
        .id(CLOSED_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      )
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
    createRequest.getAccess()
      .type(new AccessType()
        .id(EMBARGOED_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      )
      .embargoExpiry(LocalDate.now())
      .accessStatement("Embargoed");
    try {
      raidApi.createRaidV1(createRequest);
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with embargoed access type fails with missing embargoExpiry")
  void missingEmbargoExpiry() {
    createRequest.getAccess()
      .type(new AccessType()
        .id(EMBARGOED_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      )
      .accessStatement("Embargoed");
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
    createRequest.getAccess()
      .type(new AccessType()
        .id(CLOSED_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      );

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
    createRequest.getAccess()
      .type(new AccessType()
        .id(CLOSED_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      )
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
    createRequest.getAccess()
      .type(new AccessType()
        .id(OPEN_ACCESS_TYPE)
      );

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.type.schemeUri")
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
    createRequest.getAccess()
      .type(new AccessType()
        .id(OPEN_ACCESS_TYPE)
        .schemeUri("")
      );
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.type.schemeUri")
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
    createRequest.getAccess()
      .type(new AccessType()
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      );
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.type.id")
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
    createRequest.getAccess()
      .type(new AccessType()
        .id("")
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      );
    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.type.id")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }
}