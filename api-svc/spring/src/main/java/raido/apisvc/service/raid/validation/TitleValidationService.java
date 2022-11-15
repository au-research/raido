package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static raido.apisvc.endpoint.message.ValidationMessage.AT_LEAST_ONE_PRIMARY_TITLE;
import static raido.apisvc.endpoint.message.ValidationMessage.TITLES_NOT_SET;
import static raido.apisvc.endpoint.message.ValidationMessage.TOO_MANY_PRIMARY_TITLE;
import static raido.apisvc.endpoint.message.ValidationMessage.titleNotSet;
import static raido.apisvc.endpoint.message.ValidationMessage.titleStartDateNotSet;
import static raido.apisvc.endpoint.message.ValidationMessage.primaryTitleTooLong;
import static raido.apisvc.endpoint.message.ValidationMessage.titlesTypeNotSet;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitles;
import static raido.apisvc.util.JooqUtil.valueFits;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.api_svc.tables.Raid.RAID;

@Component
public class TitleValidationService {


  public List<ValidationFailure> validatePrimaryTitle(
    List<TitleBlock> titles
  ) {

    var primaryTitles = getPrimaryTitles(titles);

    if( primaryTitles.size() == 0 ){
      return of(AT_LEAST_ONE_PRIMARY_TITLE);
    }

    if( primaryTitles.size() > 1 ){
      return of(TOO_MANY_PRIMARY_TITLE);
    }

    return emptyList();
  }

  public List<ValidationFailure> validateTitles(List<TitleBlock> titles) {
    if( titles == null ) {
      return of(TITLES_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();

    failures.addAll(validatePrimaryTitle(titles));

    for( int i = 0; i < titles.size(); i++ ){
      var iTitle = titles.get(i);

      if( isBlank(iTitle.getTitle()) ){
        failures.add(titleNotSet(i));
      }
      if( !valueFits(RAID.PRIMARY_TITLE, iTitle.getTitle()) ){
        failures.add(primaryTitleTooLong(i));
      }
      if( iTitle.getType() == null ){
        failures.add(titlesTypeNotSet(i));
      }
      if( iTitle.getStartDate() == null ){
        failures.add(titleStartDateNotSet(i));
      }

    }
    return failures;
  }


}
