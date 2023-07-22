package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.ValidationFailure;
import raido.inttest.RaidApiValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TitleIntegrationTest extends AbstractStableIntegrationTest {

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
    createRequest.getTitles().get(0).setType(new TitleType()
      .id(TestConstants.ALTERNATIVE_TITLE_TYPE)
      .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI));

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
  @DisplayName("Minting a RAiD with missing schemeUri fails")
  void missingTitleScheme() {
    createRequest.getTitles().get(0).setType(new TitleType()
      .id(TestConstants.PRIMARY_TITLE_TYPE)
    );

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with missing schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("titles[0].type.schemeUri")
          .errorType("notSet")
          .message("field must be set")
      );
    } catch (Exception e) {
      fail("Expected RaidApiValidationException");
    }
  }

  @Test
  @DisplayName("Minting a RAiD with invalid schemeUri fails")
  void invalidTitleScheme() {
    createRequest.getTitles().get(0).setType(new TitleType()
      .id(TestConstants.PRIMARY_TITLE_TYPE)
      .schemeUri("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v2"));

    try {
      raidApi.createRaidV1(createRequest);
      fail("No exception thrown with invalid schemeUri");
    } catch (RaidApiValidationException e) {
      final var failures = e.getFailures();
      assertThat(failures).hasSize(1);
      assertThat(failures).contains(
        new ValidationFailure()
          .fieldId("titles[0].type.schemeUri")
          .errorType("invalidValue")
          .message("scheme is unknown/unsupported")
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
      .type(new TitleType()
        .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI)
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
      .type(new TitleType()
        .id("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/invalid.json")
        .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI)
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
          .message("id does not exist within the given scheme")
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