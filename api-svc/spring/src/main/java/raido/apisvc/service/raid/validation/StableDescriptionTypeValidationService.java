package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.DescriptionTypeRepository;
import raido.apisvc.repository.DescriptionTypeSchemeRepository;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
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

    if (isBlank(descriptionType.getId())) {
      failures.add(new ValidationFailure()
        .fieldId("descriptions[%d].type.id")
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    }

    return failures;
  }
}
