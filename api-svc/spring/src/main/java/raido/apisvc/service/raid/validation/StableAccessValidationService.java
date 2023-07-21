package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.apisvc.repository.AccessTypeRepository;
import raido.apisvc.repository.AccessTypeSchemeRepository;
import raido.idl.raidv2.model.Access;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;
@Component
public class StableAccessValidationService {
  private static final String ACCESS_TYPE_CLOSED =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json";

  private static final String ACCESS_TYPE_EMBARGOED =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/embargoed.json";

  private final AccessTypeSchemeRepository accessTypeSchemeRepository;
  private final AccessTypeRepository accessTypeRepository;

  public StableAccessValidationService(final AccessTypeSchemeRepository accessTypeSchemeRepository, final AccessTypeRepository accessTypeRepository) {
    this.accessTypeSchemeRepository = accessTypeSchemeRepository;
    this.accessTypeRepository = accessTypeRepository;
  }

  public List<ValidationFailure> validateAccess(
    Access access
  ) {
    var failures = new ArrayList<ValidationFailure>();

    if( access == null ){
      failures.add(ValidationMessage.ACCESS_NOT_SET);
    }
    else {
      if(isBlank(access.getType())){
        failures.add(
          new ValidationFailure()
            .errorType(NOT_SET_TYPE)
            .fieldId("access.type")
            .message(FIELD_MUST_BE_SET_MESSAGE)
        );
      }
      else {
        if(
          access.getType().equals(ACCESS_TYPE_CLOSED) &&
            isBlank(access.getAccessStatement())
        ){
          failures.add(
            new ValidationFailure()
              .errorType(NOT_SET_TYPE)
              .fieldId("access.accessStatement")
              .message(FIELD_MUST_BE_SET_MESSAGE)
          );
        } else if (access.getType().equals(ACCESS_TYPE_EMBARGOED) && access.getEmbargoExpiry() == null) {
          failures.add(new ValidationFailure()
            .errorType(NOT_SET_TYPE)
            .fieldId("access.embargoExpiry")
            .message(FIELD_MUST_BE_SET_MESSAGE));
        }
      }
      if (isBlank(access.getSchemeUri())) {
        failures.add(new ValidationFailure()
          .errorType(NOT_SET_TYPE)
          .fieldId("access.schemeUri")
          .message(FIELD_MUST_BE_SET_MESSAGE));
      } else {
        final var schemeUri = accessTypeSchemeRepository.findByUri(access.getSchemeUri());

        if (schemeUri.isEmpty()) {
          failures.add(new ValidationFailure()
            .fieldId("access.schemeUri")
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_SCHEME));
        } else if (!isBlank(access.getType()) && accessTypeRepository.findByUriAndSchemeId(access.getType(), schemeUri.get().getId()).isEmpty()) {
          failures.add(new ValidationFailure()
            .errorType(INVALID_VALUE_TYPE)
            .fieldId("access.type")
            .message(INVALID_ID_FOR_SCHEME));
        }
      }


    }

    return failures;
  }





}
