package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.RelatedObjectTypeRepository;
import raido.apisvc.repository.RelatedObjectTypeSchemeRepository;
import raido.idl.raidv2.model.RelatedObjectType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableRelatedObjectTypeValidationService {
  private final RelatedObjectTypeRepository relatedObjectTypeRepository;
  private final RelatedObjectTypeSchemeRepository relatedObjectTypeSchemeRepository;

  public StableRelatedObjectTypeValidationService(final RelatedObjectTypeRepository relatedObjectTypeRepository, final RelatedObjectTypeSchemeRepository relatedObjectTypeSchemeRepository) {
    this.relatedObjectTypeRepository = relatedObjectTypeRepository;
    this.relatedObjectTypeSchemeRepository = relatedObjectTypeSchemeRepository;
  }


  public List<ValidationFailure> validate(final RelatedObjectType relatedObjectType, final int index) {
    var failures = new ArrayList<ValidationFailure>();

    if (isBlank(relatedObjectType.getId())) {
      failures.add(new ValidationFailure()
        .fieldId("relatedObjects[%d].type.id".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }
    if (isBlank(relatedObjectType.getSchemeUri())) {
      failures.add(new ValidationFailure()
        .fieldId("relatedObjects[%d].type.schemeUri".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    } else {
      final var relatedObjectTypeScheme =
        relatedObjectTypeSchemeRepository.findByUri(relatedObjectType.getSchemeUri());

      if (relatedObjectTypeScheme.isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("relatedObjects[%d].type.schemeUri".formatted(index))
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_VALUE_MESSAGE)
        );
      } else if (!isBlank(relatedObjectType.getId()) &&
        relatedObjectTypeRepository.findByUriAndSchemeId(relatedObjectType.getId(), relatedObjectTypeScheme.get().getId()).isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("relatedObjects[%d].type.id".formatted(index))
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_VALUE_MESSAGE)
        );
      }
    }

    return failures;
  }
}
