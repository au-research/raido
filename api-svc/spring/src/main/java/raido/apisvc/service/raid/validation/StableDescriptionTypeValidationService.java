package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.DescriptionTypeRepository;
import raido.apisvc.repository.DescriptionTypeSchemeRepository;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableDescriptionTypeValidationService {
  private final DescriptionTypeSchemeRepository descriptionTypeSchemeRepository;
  private final DescriptionTypeRepository descriptionTypeRepository;

  public StableDescriptionTypeValidationService(final DescriptionTypeSchemeRepository descriptionTypeSchemeRepository, final DescriptionTypeRepository descriptionTypeRepository) {
    this.descriptionTypeSchemeRepository = descriptionTypeSchemeRepository;
    this.descriptionTypeRepository = descriptionTypeRepository;
  }

  public List<ValidationFailure> validate(final DescriptionType descriptionType, final int index) {
    final var failures = new ArrayList<ValidationFailure>();

    if (descriptionType == null) {
      return List.of(new ValidationFailure()
        .fieldId("descriptions[%d].type".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    if (isBlank(descriptionType.getId())) {
      failures.add(new ValidationFailure()
        .fieldId("descriptions[%d].type.id".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    if (isBlank(descriptionType.getSchemeUri())) {
      failures.add(new ValidationFailure()
        .fieldId("descriptions[%d].type.schemeUri".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    } else {
      final var descriptionTypeScheme =
        descriptionTypeSchemeRepository.findByUri(descriptionType.getSchemeUri());

      if (descriptionTypeScheme.isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("descriptions[%d].type.schemeUri".formatted(index))
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_SCHEME));
      } else if (!isBlank(descriptionType.getId()) &&
        descriptionTypeRepository.findByUriAndSchemeId(descriptionType.getId(), descriptionTypeScheme.get().getId()).isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("descriptions[%d].type.id".formatted(index))
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_ID_FOR_SCHEME));
      }
    }

    return failures;
  }
}