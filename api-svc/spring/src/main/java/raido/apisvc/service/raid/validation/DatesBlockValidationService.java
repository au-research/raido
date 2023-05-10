package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.idl.raidv2.model.DatesBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatesBlockValidationService {
  public List<ValidationFailure> validateDates(
    DatesBlock dates
  ) {
    var failures = new ArrayList<ValidationFailure>();
    if( dates == null ){
      failures.add(ValidationMessage.DATES_NOT_SET);
    }
    else {
      if( dates.getStartDate() == null ){
        failures.add(ValidationMessage.DATES_START_DATE_NOT_SET);
      }
    }
    return failures;
  }
}
