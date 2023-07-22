package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.TitleTypeRepository;
import raido.apisvc.repository.TitleTypeSchemeRepository;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableTitleTypeValidationService {
  private final TitleTypeSchemeRepository titleTypeSchemeRepository;
  private final TitleTypeRepository titleTypeRepository;

  public StableTitleTypeValidationService(final TitleTypeSchemeRepository titleTypeSchemeRepository, final TitleTypeRepository titleTypeRepository) {
    this.titleTypeSchemeRepository = titleTypeSchemeRepository;
    this.titleTypeRepository = titleTypeRepository;
  }

  public List<ValidationFailure> validate(final TitleType titleType, final int index) {
    final var failures = new ArrayList<ValidationFailure>();

    if (titleType == null) {
      return List.of(new ValidationFailure()
        .fieldId("titles[%d].type".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    if (isBlank(titleType.getId())) {
      failures.add(new ValidationFailure()
        .fieldId("titles[%d].type.id".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    if (isBlank(titleType.getSchemeUri())) {
      failures.add(new ValidationFailure()
        .fieldId("titles[%d].type.schemeUri".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    } else {
      final var titleTypeScheme =
        titleTypeSchemeRepository.findByUri(titleType.getSchemeUri());

      if (titleTypeScheme.isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("titles[%d].type.schemeUri".formatted(index))
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_SCHEME));
      } else if (!isBlank(titleType.getId()) &&
        titleTypeRepository.findByUriAndSchemeId(titleType.getId(), titleTypeScheme.get().getId()).isEmpty()) {
        failures.add(new ValidationFailure()
          .fieldId("titles[%d].type.id".formatted(index))
          .errorType(INVALID_VALUE_TYPE)
          .message(INVALID_ID_FOR_SCHEME));
      }
    }

    return failures;
  }
}