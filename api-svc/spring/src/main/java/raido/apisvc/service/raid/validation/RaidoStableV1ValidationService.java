package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.apisvc.service.raid.id.IdentifierHandle;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.List.of;
import static raido.apisvc.endpoint.message.ValidationMessage.HANDLE_DOES_NOT_MATCH_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.handlesDoNotMatch;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlDecode;
import static raido.apisvc.util.StringUtil.areDifferent;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;

@Component
public class RaidoStableV1ValidationService {
  private static final String ACCESS_TYPE_CLOSED =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json";
  private static final Log log = to(RaidoStableV1ValidationService.class);
  
  private final StableTitleValidationService titleSvc;
  private final StableDescriptionValidationService descSvc;
  private final StableContributorValidationService contribSvc;
  private final StableOrganisationValidationService orgSvc;
  private final StableAccessValidationService accessValidationService;
  private final StableSubjectValidationService subjectSvc;
  private final IdentifierParser idParser;
  private final StableRelatedObjectValidationService relatedObjectSvc;
  private final StableAlternateIdentifierValidationService alternateIdentifierSvc;
  private final StableRelatedRaidValidationService relatedRaidSvc;
  private final StableSpatialCoverageValidationService spatialCoverageSvc;
  private final StableTraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelSvc;

  public RaidoStableV1ValidationService(
    final StableTitleValidationService titleSvc,
    final StableDescriptionValidationService descSvc,
    final StableContributorValidationService contribSvc,
    final StableOrganisationValidationService orgSvc,
    final StableSubjectValidationService subjectSvc,
    final IdentifierParser idParser,
    final StableRelatedObjectValidationService relatedObjectSvc,
    final StableAlternateIdentifierValidationService alternateIdentifierSvc,
    final StableRelatedRaidValidationService relatedRaidSvc,
    final StableSpatialCoverageValidationService spatialCoverageSvc,
    final StableTraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelSvc,
    final StableAccessValidationService accessValidationService) {
    this.titleSvc = titleSvc;
    this.descSvc = descSvc;
    this.contribSvc = contribSvc;
    this.orgSvc = orgSvc;
    this.subjectSvc = subjectSvc;
    this.idParser = idParser;
    this.relatedObjectSvc = relatedObjectSvc;
    this.alternateIdentifierSvc = alternateIdentifierSvc;
    this.relatedRaidSvc = relatedRaidSvc;
    this.spatialCoverageSvc = spatialCoverageSvc;
    this.traditionalKnowledgeLabelSvc = traditionalKnowledgeLabelSvc;
    this.accessValidationService = accessValidationService;
  }

  private List<ValidationFailure> validateUpdateHandle(final String decodedHandleFromPath, final IdBlock updateIdBlock) {
    final var failures = new ArrayList<ValidationFailure>();

    IdentifierUrl updateId = null;
    try {
      updateId = idParser.parseUrlWithException(updateIdBlock.getIdentifier());
    }
    catch( ValidationFailureException e ){
      failures.addAll(e.getFailures());
    }

    IdentifierHandle pathHandle = null;
    try {
      pathHandle = idParser.parseHandleWithException(decodedHandleFromPath);
    }
    catch( ValidationFailureException e ){
      failures.addAll(e.getFailures());
    }

    if( updateId != null && pathHandle != null ){
      if( areDifferent(pathHandle.format(), updateId.handle().format()) ){
        log.with("pathHandle", pathHandle.format()).
          with("updateId", updateId.handle().format()).
          error(HANDLE_DOES_NOT_MATCH_MESSAGE);
        failures.add(handlesDoNotMatch());
      }
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

//  private List<ValidationFailure> validateAccess(
//    Access access
//  ) {
//    var failures = new ArrayList<ValidationFailure>();
//
//    if( access == null ){
//      failures.add(ValidationMessage.ACCESS_NOT_SET);
//    }
//    else {
//      if( access.getType() == null ){
//        failures.add(ValidationMessage.ACCESS_TYPE_NOT_SET);
//      }
//      else {
//        if(
//          access.getType().equals(ACCESS_TYPE_CLOSED) &&
//            access.getAccessStatement() == null
//        ){
//          failures.add(ValidationMessage.ACCESS_STATEMENT_NOT_SET);
//        }
//      }
//    }

//    return failures;
//  }
  public List<ValidationFailure> validateAlternateUrls(
    List<AlternateUrl> urls
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
    failures.addAll(accessValidationService.validate(request.getAccess()));
    failures.addAll(titleSvc.validate(request.getTitles()));
    failures.addAll(descSvc.validate(request.getDescriptions()));
    failures.addAll(validateAlternateUrls(request.getAlternateUrls()));
    failures.addAll(contribSvc.validate(request.getContributors()));
    failures.addAll(orgSvc.validate(request.getOrganisations()));
    failures.addAll(subjectSvc.validateSubjects(request.getSubjects()));
    failures.addAll(relatedRaidSvc.validateRelatedRaids(request.getRelatedRaids()));
    failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObjects()));
    failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(request.getAlternateIdentifiers()));
    failures.addAll(spatialCoverageSvc.validate(request.getSpatialCoverages()));
    failures.addAll(traditionalKnowledgeLabelSvc.validateTraditionalKnowledgeLabels(
      request.getTraditionalKnowledgeLabels()));

    return failures;
  }

  public List<ValidationFailure> validateForUpdate(final String handle, final UpdateRaidV1Request request) {
    if( request == null ){
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    String decodedHandle = urlDecode(handle);

    final var failures = new ArrayList<>(validateUpdateHandle(decodedHandle, request.getId()));

    if( request.getMetadataSchema() != RAIDOMETADATASCHEMAV1 ){
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(validateDates(request.getDates()));
    failures.addAll(accessValidationService.validate(request.getAccess()));
    failures.addAll(titleSvc.validate(request.getTitles()));
    failures.addAll(descSvc.validate(request.getDescriptions()));
    failures.addAll(validateAlternateUrls(request.getAlternateUrls()));
    failures.addAll(contribSvc.validate(request.getContributors()));
    failures.addAll(orgSvc.validate(request.getOrganisations()));
    failures.addAll(subjectSvc.validateSubjects(request.getSubjects()));
    failures.addAll(relatedRaidSvc.validateRelatedRaids(request.getRelatedRaids()));
    failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObjects()));
    failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(request.getAlternateIdentifiers()));
    failures.addAll(spatialCoverageSvc.validate(request.getSpatialCoverages()));
    failures.addAll(traditionalKnowledgeLabelSvc.validateTraditionalKnowledgeLabels(
      request.getTraditionalKnowledgeLabels()));

    return failures;
  }
}