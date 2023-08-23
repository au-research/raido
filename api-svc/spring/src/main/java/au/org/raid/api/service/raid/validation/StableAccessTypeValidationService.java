package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemeRepository;
import au.org.raid.idl.raidv2.model.AccessTypeWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class StableAccessTypeValidationService {
  private final AccessTypeSchemeRepository accessTypeSchemeRepository;
  private final AccessTypeRepository accessTypeRepository;

  public StableAccessTypeValidationService(final AccessTypeSchemeRepository accessTypeSchemeRepository, final AccessTypeRepository accessTypeRepository) {
    this.accessTypeSchemeRepository = accessTypeSchemeRepository;
    this.accessTypeRepository = accessTypeRepository;
  }

  public List<ValidationFailure> validate(final AccessTypeWithSchemeUri accessType) {
    final var failures = new ArrayList<ValidationFailure>();

    if (accessType == null) {
      return List.of(new ValidationFailure()
        .fieldId("access.type")
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    if (isBlank(accessType.getId())) {
      failures.add(new ValidationFailure()
        .fieldId("access.type.id")
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    if (isBlank(accessType.getSchemeUri())) {
      failures.add(new ValidationFailure()
        .fieldId("access.type.schemeUri")
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    } else {
      final var accessTypeScheme =
        accessTypeSchemeRepository.findByUri(accessType.getSchemeUri());

      if (accessTypeScheme.isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("access.type.schemeUri")
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_SCHEME));
      } else if (!isBlank(accessType.getId()) &&
        accessTypeRepository.findByUriAndSchemeId(accessType.getId(), accessTypeScheme.get().getId()).isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("access.type.id")
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_ID_FOR_SCHEME));
      }
    }

    return failures;
  }
}