package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.idl.raidv2.model.Access;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.util.StringUtil.isBlank;
@Component
public class StableAccessValidationService {
  private static final String ACCESS_TYPE_EMBARGOED =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/embargoed.json";
  private static final String ACCESS_TYPE_OPEN =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json";
  private final StableAccessTypeValidationService typeValidationService;

  public StableAccessValidationService(final StableAccessTypeValidationService typeValidationService) {
    this.typeValidationService = typeValidationService;
  }

  public List<ValidationFailure> validate(
    Access access
  ) {
    var failures = new ArrayList<ValidationFailure>();

    if( access == null ){
      failures.add(ValidationMessage.ACCESS_NOT_SET);
    } else if (access.getType() == null) {
      failures.add(
        new ValidationFailure()
          .errorType(NOT_SET_TYPE)
          .fieldId("access.type")
          .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    } else {
      failures.addAll(typeValidationService.validate(access.getType()));

      if (!isBlank(access.getType().getId())) {
        final var typeId = access.getType().getId();

        if (!typeId.equals(ACCESS_TYPE_OPEN) && isBlank(access.getAccessStatement())) {
          failures.add(
            new ValidationFailure()
              .errorType(NOT_SET_TYPE)
              .fieldId("access.accessStatement")
              .message(FIELD_MUST_BE_SET_MESSAGE)
          );
        }
        if (typeId.equals(ACCESS_TYPE_EMBARGOED) && access.getEmbargoExpiry() == null) {
          failures.add(new ValidationFailure()
            .errorType(NOT_SET_TYPE)
            .fieldId("access.embargoExpiry")
            .message(FIELD_MUST_BE_SET_MESSAGE));
        }
      }
    }


    return failures;
  }
}