package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitles;
import static raido.apisvc.util.JooqUtil.valueFits;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.api_svc.tables.RaidV2.RAID_V2;

@Component
public class TitleValidationService {

  public List<ValidationFailure> validatePrimaryTitle(
    List<TitleBlock> titles
  ) {

    var primaryTitles = getPrimaryTitles(titles);

    if( primaryTitles.size() == 0 ){
      return of(new ValidationFailure().
        fieldId("titles.type").
        errorType("missingPrimaryTitle").
        message("at least one primaryTitle entry must be provided") );
    }

    if( primaryTitles.size() > 1 ){
      return of(new ValidationFailure().
        fieldId("titles.type").
        errorType("tooManyPrimaryTitle").
        message("too many primaryTitle entries provided") );
    }

    return emptyList();
  }

  public List<ValidationFailure> validateTitles(List<TitleBlock> titles) {
    if( titles == null ) {
      return of(new ValidationFailure().
        fieldId("titles").
        errorType("notSet").
        message("field must be set") );
    }

    var failures = new ArrayList<ValidationFailure>();

    failures.addAll(validatePrimaryTitle(titles));

    for( int i = 0; i < titles.size(); i++ ){
      var iTitle = titles.get(i);

      if( isBlank(iTitle.getTitle()) ){
        failures.add(new ValidationFailure().
          fieldId("titles[%s].title".formatted(i)).
          errorType("notSet").
          message("field must be set") );
      }
      if( !valueFits(RAID_V2.PRIMARY_TITLE, iTitle.getTitle()) ){
        failures.add(new ValidationFailure().
          fieldId("titles[%s].title".formatted(i)).
          errorType("tooLong").
          message("field must fit in length: " +
            RAID_V2.PRIMARY_TITLE.getDataType().length() ));
      }
      if( iTitle.getType() == null ){
        failures.add(new ValidationFailure().
          fieldId("titles[%s].type".formatted(i)).
          errorType("notSet").
          message("field must be set") );
      }
      if( iTitle.getStartDate() == null ){
        failures.add(new ValidationFailure().
          fieldId("titles[%s].startDate".formatted(i)).
          errorType("notSet").
          message("field must be set") );
      }

    }
    return failures;
  }  
}
