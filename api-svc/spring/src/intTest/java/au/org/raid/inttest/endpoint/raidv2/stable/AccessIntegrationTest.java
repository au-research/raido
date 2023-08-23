package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.AccessTypeWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class AccessIntegrationTest extends AbstractIntegrationTest {

  @Test
  @DisplayName("Mint with invalid language id fails")
  void invalidLanguageId() {
    createRequest.getAccess().getAccessStatement().getLanguage().setId("xxx");

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
              new ValidationFailure()
                      .fieldId("access.accessStatement.language.id")
                      .errorType("invalidValue")
                      .message("id does not exist within the given scheme")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with invalid language schemeUri fails")
  void invalidLanguageSchemeUri() {
    createRequest.getAccess().getAccessStatement().getLanguage().setSchemeUri("http://localhost");

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
              new ValidationFailure()
                      .fieldId("access.accessStatement.language.schemeUri")
                      .errorType("invalidValue")
                      .message("scheme is unknown/unsupported")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }
  @Test
  @DisplayName("Mint with empty language schemeUri fails")
  void nullLanguageSchemeUri() {
    createRequest.getAccess().getAccessStatement().getLanguage().setSchemeUri(null);

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
              new ValidationFailure()
                      .fieldId("access.accessStatement.language.schemeUri")
                      .errorType("notSet")
                      .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with empty language schemeUri fails")
  void emptyLanguageSchemeUri() {
    createRequest.getAccess().getAccessStatement().getLanguage().setSchemeUri("");

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
              new ValidationFailure()
                      .fieldId("access.accessStatement.language.schemeUri")
                      .errorType("notSet")
                      .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with empty language id fails")
  void emptyLanguageId() {
    createRequest.getAccess().getAccessStatement().getLanguage().setId("");

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
              new ValidationFailure()
                      .fieldId("access.accessStatement.language.id")
                      .errorType("notSet")
                      .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint with null language id fails")
  void nullLanguageId() {
    createRequest.getAccess().getAccessStatement().getLanguage().setId(null);

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
              new ValidationFailure()
                      .fieldId("access.accessStatement.language.id")
                      .errorType("notSet")
                      .message("field must be set")
      );
    } catch (Exception e) {
      fail("Mint should be successful");
    }
  }

  @Test
  @DisplayName("Mint raid with valid open access type")
  void mintOpenAccess() {
    createRequest.getAccess()
      .type(new AccessTypeWithSchemeUri()
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
            .type(new AccessTypeWithSchemeUri()
                    .id(CLOSED_ACCESS_TYPE)
                    .schemeUri(ACCESS_TYPE_SCHEME_URI)
            )
            .accessStatement(new AccessStatement().statement("Closed"));
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
            .type(new AccessTypeWithSchemeUri()
                    .id(EMBARGOED_ACCESS_TYPE)
                    .schemeUri(ACCESS_TYPE_SCHEME_URI)
            )
            .embargoExpiry(LocalDate.now())
            .accessStatement(new AccessStatement().statement("Embargoed"));
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
            .type(new AccessTypeWithSchemeUri()
                    .id(EMBARGOED_ACCESS_TYPE)
                    .schemeUri(ACCESS_TYPE_SCHEME_URI)
            )
            .accessStatement(new AccessStatement().statement("Embargoed"));
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
      .type(new AccessTypeWithSchemeUri()
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
      .type(new AccessTypeWithSchemeUri()
        .id(CLOSED_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      )
      .accessStatement(new AccessStatement().statement(""));

    try {
      raidApi.createRaidV1(createRequest);
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("access.accessStatement.statement")
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
      .type(new AccessTypeWithSchemeUri()
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
      .type(new AccessTypeWithSchemeUri()
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
      .type(new AccessTypeWithSchemeUri()
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
      .type(new AccessTypeWithSchemeUri()
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