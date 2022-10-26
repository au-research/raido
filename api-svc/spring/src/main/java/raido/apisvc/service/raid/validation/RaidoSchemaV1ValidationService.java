package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.DatesBlock;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;
import static raido.apisvc.service.raid.MetadataService.Schema.RAIDO_V1;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.areEqual;

@Component
public class RaidoSchemaV1ValidationService {
  private static final Log log = to(RaidoSchemaV1ValidationService.class);

  private TitleValidationService titleSvc;

  public RaidoSchemaV1ValidationService(TitleValidationService titleSvc) {
    this.titleSvc = titleSvc;
  }

  public List<ValidationFailure> validateRaidoSchemaV1(
    RaidoMetadataSchemaV1 metadata
  ) {
    if( metadata == null ) {
      return of(new ValidationFailure().
        fieldId("metadata").
        errorType("notSet").
        message("field must be set") );
    }

    var failures = new ArrayList<ValidationFailure>();
    if( !areEqual(metadata.getMetadataSchema(), RAIDO_V1.getId()) ) {
      failures.add(new ValidationFailure().
        fieldId("metadata.metadataSchema").
        errorType("invalidSchema").
        message("has unsupported value") );
    }

    failures.addAll(validateDates(metadata.getDates()));

    failures.addAll(validateAccess(metadata.getAccess()));

    failures.addAll(titleSvc.validateTitles(metadata.getTitles()));

    return failures;
  }

  private static List<ValidationFailure> validateDates(
    DatesBlock dates
  ) {
    var failures = new ArrayList<ValidationFailure>();
    if( dates == null ) {
      failures.add(new ValidationFailure().
        fieldId("metadata.dates").
        errorType("notSet").
        message("field must be set") );
    }
    else {
      if( dates.getStartDate() == null ){
        failures.add(new ValidationFailure().
          fieldId("metadata.dates.start").
          errorType("notSet").
          message("field must be set") );
      }
    }
    return failures;
  }

  private static List<ValidationFailure> validateAccess(
    AccessBlock access
  ) {
    var failures = new ArrayList<ValidationFailure>();
    
    if( access == null ) {
      failures.add(new ValidationFailure().
        fieldId("metadata.access").
        errorType("notSet").
        message("field must be set") );
    }
    else {
      if( access.getType() == null ){
        failures.add(new ValidationFailure().
          fieldId("metadata.access.type").
          errorType("notSet").
          message("field must be set") );
      }
      else {
        if( access.getType() == AccessType.CLOSED ){
          if( access.getAccessStatement() == null ){
            failures.add(new ValidationFailure().
              fieldId("metadata.access.accessStatement").
              errorType("notSet").
              message("field must be set if type is closed") );
          }
        }
      }
    }
    
    return failures;
  }


}
