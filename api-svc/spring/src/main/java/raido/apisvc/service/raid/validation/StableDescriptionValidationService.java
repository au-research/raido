package raido.apisvc.service.raid.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Description;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class StableDescriptionValidationService {

  private final StableDescriptionTypeValidationService typeValidationService;
  private final LanguageValidationService languageValidationService;

  public List<ValidationFailure> validate(
    List<Description> descriptions
  ) {
    if( descriptions == null ) {
      // allowed to have no desc, not sure if parser will pass through null
      // or empty if property is not set at all - either way, it's allowed
      return Collections.emptyList();
    }

    var failures = new ArrayList<ValidationFailure>();

    IntStream.range(0, descriptions.size()).forEach(index -> {
      final var description = descriptions.get(index);

      if (isBlank(description.getDescription())) {
        failures.add(new ValidationFailure()
            .fieldId("descriptions[%d].description".formatted(index))
            .errorType(NOT_SET_TYPE)
            .message(FIELD_MUST_BE_SET_MESSAGE)
        );
      }

      failures.addAll(typeValidationService.validate(description.getType(), index));
      failures.addAll(
          languageValidationService.validate(description.getLanguage(), "descriptions[%d]".formatted(index))
      );
    });

    return failures;
  }
}