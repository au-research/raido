package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.ValidationFailure;
import raido.inttest.RaidApiValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TitleIntegrationTest extends AbstractStableIntegrationTest {
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
      assertThat(failures.get(0).getFieldId()).isEqualTo("titles.type");
      assertThat(failures.get(0).getErrorType()).isEqualTo("missingPrimaryTitle");
      assertThat(failures.get(0).getMessage()).isEqualTo("at least one primaryTitle entry must be provided");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }


  }

  @Test
  @DisplayName("Minting a RAiD with only alternative title fails")
  void alternativeTitleOnly() {
    createRequest.getTitles().get(0).setType(TestConstants.ALTERNATIVE_TITLE_TYPE);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing primary title");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("titles.type");
      assertThat(failures.get(0).getErrorType()).isEqualTo("missingPrimaryTitle");
      assertThat(failures.get(0).getMessage()).isEqualTo("at least one primaryTitle entry must be provided");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Minting a RAiD with missing schemeUri fails")
  void missingTitleScheme() {
    createRequest.getTitles().get(0).setSchemeUri(null);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("titles[0].schemeUri");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Minting a RAiD with invalid schemeUri fails")
  void invalidTitleScheme() {
    createRequest.getTitles().get(0)
      .setSchemeUri("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v2");

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with invalid schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures.get(0).getFieldId()).isEqualTo("titles[0].schemeUri");
      assertThat(failures.get(0).getErrorType()).isEqualTo("invalidValue");
      assertThat(failures.get(0).getMessage()).isEqualTo("has invalid/unsupported value");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Minting a RAiD with missing title type fails")
  void missingTitleType() {
    final var titles = new ArrayList<>(createRequest.getTitles());

    titles.add(new Title()
      .title("Test Title")
      .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI)
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
          .message("field must be set")
          .fieldId("titles[1].type")
          .errorType("notSet")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Minting a RAiD with invalid title type fails")
  void invalidTitleType() {
    final var titles = new ArrayList<>(createRequest.getTitles());

    titles.add(new Title()
      .title("Test Title")
      .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI)
      .type("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/invalid.json")
      .startDate(LocalDate.now())
    );

    createRequest.setTitles(titles);

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing invalid title type");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .message("has invalid/unsupported value")
          .fieldId("titles[1].type")
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
      assertThat(failures.get(0).getFieldId()).isEqualTo("titles[0].startDate");
      assertThat(failures.get(0).getErrorType()).isEqualTo("notSet");
      assertThat(failures.get(0).getMessage()).isEqualTo("field must be set");
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }
}