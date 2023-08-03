package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.RelatedRaid;
import raido.idl.raidv2.model.RelatedRaidType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.DESCRIPTION_TYPE_SCHEME_URI;
import static raido.apisvc.util.TestConstants.PRIMARY_DESCRIPTION_TYPE;


@ExtendWith(MockitoExtension.class)
class StableRelatedRaidValidationServiceTest {
  private static final String ID = "https://raid.org/10.121221/73864387";

  @Mock
  private StableRelatedRaidTypeValidationService typeValidationService;

  @InjectMocks
  private StableRelatedRaidValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid related raid")
  void validRelatedRaid() {
    final var type = new RelatedRaidType()
        .id(PRIMARY_DESCRIPTION_TYPE)
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var relatedRaid = new RelatedRaid()
        .id(ID)
        .type(type);

    final var failures = validationService.validate(List.of(relatedRaid));

    assertThat(failures, empty());

    verify(typeValidationService).validate(type, 0);
  }

  @Test
  @DisplayName("Validation fails with null id")
  void nullRelatedRaid() {
    final var type = new RelatedRaidType()
        .id(PRIMARY_DESCRIPTION_TYPE)
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var relatedRaid = new RelatedRaid()
        .type(type);

    final var failures = validationService.validate(List.of(relatedRaid));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
        new ValidationFailure()
            .fieldId("relatedRaids[0].id")
            .errorType("notSet")
            .message("field must be set")
    ));
    verify(typeValidationService).validate(type, 0);
  }

  @Test
  @DisplayName("Validation fails with empty id")
  void emptyRelatedRaid() {
    final var relatedRaid = new RelatedRaid()
        .id("")
        .type(new RelatedRaidType()
            .id(PRIMARY_DESCRIPTION_TYPE)
            .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
        );

    final var failures = validationService.validate(List.of(relatedRaid));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
        new ValidationFailure()
            .fieldId("relatedRaids[0].id")
            .errorType("notSet")
            .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Type validation failures are returned")
  void typeErrorAreReturned() {
    final var type = new RelatedRaidType()
        .id(PRIMARY_DESCRIPTION_TYPE)
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var relatedRaid = new RelatedRaid()
        .id(ID)
        .type(type);

    final var typeError = new ValidationFailure();

    when(typeValidationService.validate(type, 0)).thenReturn(List.of(typeError));

    final var failures = validationService.validate(List.of(relatedRaid));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(typeError));
  }
}