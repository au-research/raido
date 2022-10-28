package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.DatesBlock;
import raido.idl.raidv2.model.MetadataSchemaV1;
import raido.idl.raidv2.model.Metaschema;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;

@Component
public class RaidoSchemaV1ValidationService {

  private TitleValidationService titleSvc;

  public RaidoSchemaV1ValidationService(TitleValidationService titleSvc) {
    this.titleSvc = titleSvc;
  }

  public List<ValidationFailure> validateRaidoSchemaV1(
    MetadataSchemaV1 metadata
  ) {
    if( metadata == null ) {
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();
    if( metadata.getMetadataSchema() != Metaschema.RAIDO_METADATA_SCHEMA_V1 ) {
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(validateDates(metadata.getDates()));

    failures.addAll(validateAccess(metadata.getAccess()));

    failures.addAll(titleSvc.validateTitles(metadata.getTitles()));
    
    // todo:sto validate descriptions

    return failures;
  }

  private static List<ValidationFailure> validateDates(
    DatesBlock dates
  ) {
    var failures = new ArrayList<ValidationFailure>();
    if( dates == null ) {
      failures.add(ValidationMessage.DATES_NOT_SET);
    }
    else {
      if( dates.getStartDate() == null ){
        failures.add(ValidationMessage.DATES_START_DATE_NOT_SET);
      }
    }
    return failures;
  }

  private static List<ValidationFailure> validateAccess(
    AccessBlock access
  ) {
    var failures = new ArrayList<ValidationFailure>();
    
    if( access == null ) {
      failures.add(ValidationMessage.ACCESS_NOT_SET);
    }
    else {
      if( access.getType() == null ){
        failures.add(ValidationMessage.ACCESS_TYPE_NOT_SET);
      }
      else {
        if( 
          access.getType() == AccessType.CLOSED && 
            access.getAccessStatement() == null 
        ){
          failures.add(ValidationMessage.ACCESS_STATEMENT_NOT_SET);
        }
      }
    }
    
    return failures;
  }


}
