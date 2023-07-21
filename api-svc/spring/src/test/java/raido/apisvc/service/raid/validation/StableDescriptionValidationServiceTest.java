package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.DescriptionTypeRepository;
import raido.apisvc.repository.DescriptionTypeSchemeRepository;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeRecord;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeSchemeRecord;
import raido.idl.raidv2.model.Description;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StableDescriptionValidationServiceTest {
  private static final String PRIMARY_DESCRIPTION_TYPE_ID =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json";
  private static final String DESCRIPTION_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1";

  private static final int DESCRIPTION_TYPE_SCHEME_ID = 1;

  private static final DescriptionTypeSchemeRecord DESCRIPTION_TYPE_SCHEME_RECORD =
    new DescriptionTypeSchemeRecord()
      .setUri(DESCRIPTION_TYPE_SCHEME_URI)
      .setId(DESCRIPTION_TYPE_SCHEME_ID);

  private static final DescriptionTypeRecord DESCRIPTION_TYPE_RECORD =
    new DescriptionTypeRecord()
      .setUri(PRIMARY_DESCRIPTION_TYPE_ID)
      .setSchemeId(DESCRIPTION_TYPE_SCHEME_ID);

  @Mock
  private DescriptionTypeSchemeRepository descriptionTypeSchemeRepository;

  @Mock
  private DescriptionTypeRepository descriptionTypeRepository;

  @InjectMocks
  private StableDescriptionValidationService validationService;

  @Test
  @DisplayName("Validation fails if schemeUri is null")
  void nullSchemeUri() {
    final var type = new DescriptionType()
      .id(PRIMARY_DESCRIPTION_TYPE_ID)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var description = new Description()
      .description("test description")
      .type(type);

    final var failures = validationService.validateDescriptions(List.of(description));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[0].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(descriptionTypeSchemeRepository);
    verifyNoInteractions(descriptionTypeRepository);
  }

  @Test
  @DisplayName("Validation fails if schemeUri is blank")
  void blankSchemeUri() {
    final var descriptions = List.of(new Description()
      .description("test description")
      .type(PRIMARY_DESCRIPTION_TYPE_ID)
      .schemeUri("")
    );

    final var failures = validationService.validateDescriptions(descriptions);

    assertThat(failures.size(), is(1));
    assertThat(failures.get(0).getFieldId(), is("descriptions[0].schemeUri"));
    assertThat(failures.get(0).getMessage(), is("field must be set"));
    assertThat(failures.get(0).getErrorType(), is("notSet"));

    verifyNoInteractions(descriptionTypeSchemeRepository);
    verifyNoInteractions(descriptionTypeRepository);
  }

  @Test
  @DisplayName("Validation fails if type is null")
  void nullType() {
    final var descriptions = List.of(new Description()
      .description("test description")
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
    );

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    final List<ValidationFailure> failures = validationService.validateDescriptions(descriptions);

    assertThat(failures.size(), is(1));
    assertThat(failures.get(0).getFieldId(), is("descriptions[0].type"));
    assertThat(failures.get(0).getMessage(), is("field must be set"));
    assertThat(failures.get(0).getErrorType(), is("notSet"));
    verifyNoInteractions(descriptionTypeRepository);
  }

  @Test
  @DisplayName("Validation fails if type is blank")
  void blankType() {
    final var descriptions = List.of(new Description()
      .description("test description")
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
      .type("")
    );

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    final List<ValidationFailure> failures = validationService.validateDescriptions(descriptions);

    assertThat(failures.size(), is(1));
    assertThat(failures.get(0).getFieldId(), is("descriptions[0].type"));
    assertThat(failures.get(0).getMessage(), is("field must be set"));
    assertThat(failures.get(0).getErrorType(), is("notSet"));
    verifyNoInteractions(descriptionTypeRepository);
  }

  @Test
  @DisplayName("Validation fails if description is missing")
  void missingDescription() {
    final var descriptions = List.of(new Description()
      .type(PRIMARY_DESCRIPTION_TYPE_ID)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
    );

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    when(descriptionTypeRepository.findByUriAndSchemeId(PRIMARY_DESCRIPTION_TYPE_ID, DESCRIPTION_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_RECORD));

    final List<ValidationFailure> failures = validationService.validateDescriptions(descriptions);

    assertThat(failures.size(), is(1));
    assertThat(failures.get(0).getFieldId(), is("descriptions[0].description"));
    assertThat(failures.get(0).getMessage(), is("field must be set"));
    assertThat(failures.get(0).getErrorType(), is("notSet"));
  }

  @Test
  @DisplayName("Validation fails if scheme uri does not exist")
  void invalidSchemeUri() {
    final var descriptions = List.of(new Description()
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
      .type("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json")
      .description("test description")
    );

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final List<ValidationFailure> failures = validationService.validateDescriptions(descriptions);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[0].schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));
    verifyNoInteractions(descriptionTypeRepository);
  }

  @Test
  @DisplayName("Validation fails if type does not exist in scheme")
  void invalidTypeForScheme() {
    final var type = new DescriptionType()
      .id("https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json")
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)

    final var descriptions = List.of(new Description()
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
      .type()
      .description("test description")
    );

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    when(descriptionTypeRepository.findByUriAndSchemeId(PRIMARY_DESCRIPTION_TYPE_ID, DESCRIPTION_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final List<ValidationFailure> failures = validationService.validateDescriptions(descriptions);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[0].type")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }
}