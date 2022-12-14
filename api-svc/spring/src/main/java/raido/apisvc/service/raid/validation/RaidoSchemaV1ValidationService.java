package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.apisvc.util.Log;
import raido.apisvc.util.ObjectUtil;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.AlternateUrlBlock;
import raido.idl.raidv2.model.DatesBlock;
import raido.idl.raidv2.model.IdBlock;
import raido.idl.raidv2.model.LegacyMetadataSchemaV1;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.List.of;
import static raido.apisvc.endpoint.message.ValidationMessage.fieldCannotChange;
import static raido.apisvc.endpoint.raidv2.PublicExperimental.HANDLE_SEPERATOR;
import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.areEqual;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;

@Component
public class RaidoSchemaV1ValidationService {
  private static final Log log = to(RaidoSchemaV1ValidationService.class);

  private TitleValidationService titleSvc;
  private DescriptionValidationService descSvc;
  private ContributorValidationService contribSvc;
  
  public RaidoSchemaV1ValidationService(
    TitleValidationService titleSvc,
    DescriptionValidationService descSvc,
    ContributorValidationService contribSvc
  ) {
    this.titleSvc = titleSvc;
    this.descSvc = descSvc;
    this.contribSvc = contribSvc;
  }

  /**
   Does not currently validate the ID block.
   */
  public List<ValidationFailure> validateRaidoSchemaV1(
    RaidoMetadataSchemaV1 metadata
  ) {
    if( metadata == null ){
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();
    if( metadata.getMetadataSchema() != RAIDOMETADATASCHEMAV1 ){
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(validateDates(metadata.getDates()));
    failures.addAll(validateAccess(metadata.getAccess()));
    failures.addAll(titleSvc.validateTitles(metadata.getTitles()));
    failures.addAll(descSvc.validateDescriptions(metadata.getDescriptions()));
    failures.addAll(validateAlternateUrls(metadata.getAlternateUrls()));
    failures.addAll(contribSvc.validateContributors(metadata.getContributors()));

    return failures;
  }

  public List<ValidationFailure> validateLegacySchemaV1(
    LegacyMetadataSchemaV1 metadata
  ) {
    if( metadata == null ){
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();
    if( metadata.getMetadataSchema() != LEGACYMETADATASCHEMAV1 ){
      log.with("metadataSchema", metadata.getMetadataSchema()).
        warn("attempted to migrate non-legacy schema");
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(validateDates(metadata.getDates()));
    failures.addAll(validateAccess(metadata.getAccess()));
    failures.addAll(titleSvc.validateTitles(metadata.getTitles()));
    failures.addAll(descSvc.validateDescriptions(metadata.getDescriptions()));
    failures.addAll(validateAlternateUrls(metadata.getAlternateUrls()));

    return failures;
  }

  private static List<ValidationFailure> validateDates(
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

  private static List<ValidationFailure> validateAccess(
    AccessBlock access
  ) {
    var failures = new ArrayList<ValidationFailure>();

    if( access == null ){
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


  public List<ValidationFailure> validateIdBlockForMigration(IdBlock id) {
    var failures = new ArrayList<ValidationFailure>();
    if( id == null ){
      failures.add(ValidationMessage.ID_BLOCK_NOT_SET);
      return failures;
    }

    if( isBlank(id.getIdentifier()) ){
      failures.add(ValidationMessage.IDENTIFIER_NOT_SET);
    }
    else {
      var handleSplit = id.getIdentifier().split(HANDLE_SEPERATOR);
      if( handleSplit.length == 2 ){
        if( isBlank(handleSplit[0]) ){
          log.with("handle", id.getIdentifier()).
            warn("handle prefix was blank");
          failures.add(ValidationMessage.IDENTIFIER_INVALID);
        }
        if( isBlank(handleSplit[1]) ){
          log.with("handle", id.getIdentifier()).
            warn("handle suffix was blank");
          failures.add(ValidationMessage.IDENTIFIER_INVALID);
        }
      }
      else {
        log.with("handle", id.getIdentifier()).with("split", handleSplit).
          warn("attempted to import invalid handle");
        failures.add(ValidationMessage.IDENTIFIER_INVALID);
      }
    }

    // improve: would be good to validate the handle format
    // at least that prefix and suffix exist, separated by slash

    if( !ObjectUtil.areEqual(id.getIdentifierTypeUri(), RAID_ID_TYPE_URI) ){
      failures.add(ValidationMessage.ID_TYPE_URI_INVALID);
    }

    if( isBlank(id.getGlobalUrl()) ){
      failures.add(ValidationMessage.GLOBAL_URL_NOT_SET);
    }

    // don't need client to send raidAgency fields, we'll set them on insert

    return failures;
  }

  public List<ValidationFailure> validateIdBlockNotChanged(
    IdBlock newId,
    IdBlock oldId
  ) {
    List<ValidationFailure> failures = new ArrayList<>();

    if( !areEqual(newId.getIdentifier(), oldId.getIdentifier()) ){
      failures.add(fieldCannotChange("metadata.id.identifier"));
    }
    if( !areEqual(newId.getIdentifierTypeUri(), oldId.getIdentifierTypeUri()) ){
      failures.add(fieldCannotChange("metadata.id.identifierTypeUri"));
    }
    if( !areEqual(newId.getGlobalUrl(), oldId.getGlobalUrl()) ){
      failures.add(fieldCannotChange("metadata.id.globalUrl"));
    }
    if( !areEqual(newId.getRaidAgencyUrl(), oldId.getRaidAgencyUrl()) ){
      failures.add(fieldCannotChange("metadata.id.raidAgencyUrl"));
    }
    if( !areEqual(
      newId.getRaidAgencyIdentifier(),
      oldId.getRaidAgencyIdentifier())
    ){
      failures.add(fieldCannotChange("metadata.id.raidAgencyIdentifier"));
    }

    return failures;
  }

  public List<ValidationFailure> validateAlternateUrls(
    List<AlternateUrlBlock> urls
  ) {
    if( urls == null ){
      return Collections.emptyList();
    }

    var failures = new ArrayList<ValidationFailure>();

    for( int i = 0; i < urls.size(); i++ ){
      var iUrl = urls.get(i);

      if( isBlank(iUrl.getUrl()) ){
        failures.add(ValidationMessage.alternateUrlNotSet(i));
      }

      /* not sure yet if we want to be doing any further validation of this 
      - only https, or only http/https?
      - must be a formal URI?
      - url must exist (ping if it returns a 200 response?) */
    }
    return failures;
  }

}