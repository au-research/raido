package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.DescriptionTypeRepository;
import au.org.raid.api.repository.DescriptionTypeSchemeRepository;
import au.org.raid.idl.raidv2.model.DescriptionTypeWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class StableDescriptionTypeValidationService {
  private final DescriptionTypeSchemeRepository descriptionTypeSchemeRepository;
  private final DescriptionTypeRepository descriptionTypeRepository;

  public List<ValidationFailure> validate(final DescriptionTypeWithSchemeUri descriptionType, final int index) {
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