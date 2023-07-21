package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.DescriptionTypeRepository;
import raido.apisvc.repository.DescriptionTypeSchemeRepository;
import raido.idl.raidv2.model.Description;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StableDescriptionValidationService {

  private final DescriptionTypeSchemeRepository descriptionTypeSchemeRepository;
  private final DescriptionTypeRepository descriptionTypeRepository;

  public StableDescriptionValidationService(final DescriptionTypeSchemeRepository descriptionTypeSchemeRepository,
                                            final DescriptionTypeRepository descriptionTypeRepository) {
    this.descriptionTypeSchemeRepository = descriptionTypeSchemeRepository;
    this.descriptionTypeRepository = descriptionTypeRepository;
  }

  public List<ValidationFailure> validateDescriptions(
    List<Description> descriptions
  ) {
    if( descriptions == null ) {
      // allowed to have no desc, not sure if parser will pass through null
      // or empty if property is not set at all - either way, it's allowed
      return Collections.emptyList();
    }

    var failures = new ArrayList<ValidationFailure>();

//    for( int i = 0; i < descriptions.size(); i++ ){
//      var description = descriptions.get(i);
//      if( isBlank(description.getDescription()) ){
//        failures.add(ValidationMessage.descriptionNotSet(i));
//      }
//      if (isBlank(description.getType())) {
//        failures.add(descriptionTypeNotSet(i));
//      }
//
//      if (isBlank(description.getSchemeUri())) {
//        failures.add(new ValidationFailure().
//          fieldId("descriptions[%s].schemeUri".formatted(i)).
//          errorType(NOT_SET_TYPE).
//          message(FIELD_MUST_BE_SET_MESSAGE));
//      }
//      else {
//        var schemeUri =
//          descriptionTypeSchemeRepository.findByUri(description.getSchemeUri());
//
//        if (schemeUri.isEmpty()){
//          failures.add(new ValidationFailure().
//            fieldId("descriptions[%s].schemeUri".formatted(i)).
//            errorType(INVALID_VALUE_TYPE).
//            message(INVALID_SCHEME));
//        } else if (!isBlank(description.getType()) && descriptionTypeRepository.findByUriAndSchemeId(description.getType(), schemeUri.get().getId()).isEmpty()) {
//          failures.add(new ValidationFailure().
//            fieldId("descriptions[%s].type".formatted(i)).
//            errorType(INVALID_VALUE_TYPE).
//            message(INVALID_ID_FOR_SCHEME));
//        }
//      }
//    }
    return failures;
  }
}
