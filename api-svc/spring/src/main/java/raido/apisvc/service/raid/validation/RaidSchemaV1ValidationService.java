package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.List.of;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;

@Component
public class RaidSchemaV1ValidationService {
  private final TitleValidationService titleSvc;
  private final DescriptionValidationService descSvc;
  private final ContributorValidationService contribSvc;

  private final OrganisationValidationService orgSvc;

  private final SubjectValidationService subjectSvc;

  public RaidSchemaV1ValidationService(
    TitleValidationService titleSvc,
    DescriptionValidationService descSvc,
    ContributorValidationService contribSvc,
    OrganisationValidationService orgSvc,
    final SubjectValidationService subjectSvc) {
    this.titleSvc = titleSvc;
    this.descSvc = descSvc;
    this.contribSvc = contribSvc;
    this.orgSvc = orgSvc;
    this.subjectSvc = subjectSvc;
  }

  private static List<ValidationFailure> validateHandle(final String handle, final IdBlock idBlock) {
    final var failures = new ArrayList<ValidationFailure>();

    if (!handle.equals(idBlock.getIdentifier())) {
      failures.add(ValidationMessage.handlesDoNotMatch());
    }

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

  public List<ValidationFailure> validateForCreate(final CreateRaidV1Request request) {
    if( request == null ){
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();
    if( request.getMetadataSchema() != RAIDOMETADATASCHEMAV1 ){
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(validateDates(request.getDates()));
    failures.addAll(validateAccess(request.getAccess()));
    failures.addAll(titleSvc.validateTitles(request.getTitles()));
    failures.addAll(descSvc.validateDescriptions(request.getDescriptions()));
    failures.addAll(validateAlternateUrls(request.getAlternateUrls()));
    failures.addAll(contribSvc.validateContributors(request.getContributors()));
    failures.addAll(orgSvc.validateOrganisations(request.getOrganisations()));
    failures.addAll(subjectSvc.validateSubjects(request.getSubjects()));


    return failures;
  }

  public List<ValidationFailure> validateForUpdate(final String handle, final UpdateRaidV1Request request) {
    if( request == null ){
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    final var failures = new ArrayList<>(validateHandle(handle, request.getId()));

    if( request.getMetadataSchema() != RAIDOMETADATASCHEMAV1 ){
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(validateDates(request.getDates()));
    failures.addAll(validateAccess(request.getAccess()));
    failures.addAll(titleSvc.validateTitles(request.getTitles()));
    failures.addAll(descSvc.validateDescriptions(request.getDescriptions()));
    failures.addAll(validateAlternateUrls(request.getAlternateUrls()));
    failures.addAll(contribSvc.validateContributors(request.getContributors()));
    failures.addAll(orgSvc.validateOrganisations(request.getOrganisations()));
    failures.addAll(subjectSvc.validateSubjects(request.getSubjects()));

    return failures;
  }
}