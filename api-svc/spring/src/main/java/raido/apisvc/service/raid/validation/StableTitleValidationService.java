package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.JooqUtil.valueFits;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.api_svc.tables.Raid.RAID;

@Component
public class StableTitleValidationService {

  private static final String PRIMARY_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

  public List<ValidationFailure> validatePrimaryTitle(
    List<Title> titles
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

  public List<ValidationFailure> validateTitles(List<Title> titles) {
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


  public List<Title> getPrimaryTitles(List<Title> titles) {
    return titles.stream().
      filter(i->i.getType().equals(PRIMARY_TITLE_TYPE)).toList();
  }

  /**
   Should only call be called with validated metadata.
   @throws java.util.NoSuchElementException if no primary title is present
   */
  public TitleBlock getPrimaryTitle(List<TitleBlock> titles) {
    return titles.stream().
      filter(i->i.getType() == TitleType.PRIMARY_TITLE).
      findFirst().orElseThrow();
  }

}
