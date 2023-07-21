package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.DescriptionTypeRepository;
import raido.apisvc.repository.DescriptionTypeSchemeRepository;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.ValidationFailure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static raido.apisvc.util.TestConstants.DESCRIPTION_TYPE_SCHEME_URI;
import static raido.apisvc.util.TestConstants.PRIMARY_DESCRIPTION_TYPE;

@ExtendWith(MockitoExtension.class)
class StableDescriptionTypeValidationServiceTest {
  private static final int INDEX = 3;
  @Mock
  private DescriptionTypeSchemeRepository descriptionTypeSchemeRepository;
  @Mock
  private DescriptionTypeRepository descriptionTypeRepository;
  @InjectMocks
  private StableDescriptionTypeValidationService validationService;

  @Test
  @DisplayName("Validation fails when id is null")
  void nullId() {
    final var descriptionType = new DescriptionType()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.id")
        .errorType("notSet")
        .message("")
    ));

  }
}