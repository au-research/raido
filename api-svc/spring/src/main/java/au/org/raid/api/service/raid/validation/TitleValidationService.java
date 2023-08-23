package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.raid.RaidoSchemaV1Util;
import au.org.raid.idl.raidv2.model.TitleBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.JooqUtil.valueFits;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.db.jooq.api_svc.tables.Raid.RAID;
import static java.util.Collections.emptyList;
import static java.util.List.of;

@Component
public class TitleValidationService {


  public List<ValidationFailure> validatePrimaryTitle(
    List<TitleBlock> titles
  ) {

    var primaryTitles = RaidoSchemaV1Util.getPrimaryTitles(titles);

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
