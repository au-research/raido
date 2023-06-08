package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.message.ValidationMessage;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.UpdateRaidStableV2Request;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlDecode;

@Component
public class RaidoStableV2ValidationService {
  private static final Log log = to(RaidoStableV2ValidationService.class);

  private final TitleValidationService titleSvc;
  private final DescriptionValidationService descSvc;
  private final ContributorValidationService contribSvc;
  private final OrganisationValidationService orgSvc;
  private final SubjectValidationService subjectSvc;
  private final RelatedObjectValidationService relatedObjectSvc;
  private final AlternateIdentifierValidationService alternateIdentifierSvc;
  private final RelatedRaidValidationService relatedRaidSvc;
  private final SpatialCoverageValidationService spatialCoverageSvc;
  private final TraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelSvc;
  private final AccessBlockValidationService accessBlockValidationService;
  private final AlternateUrlBlockValidationService alternateUrlBlockValidationService;
  private final IdBlockV2ValidationService idBlockV2ValidationService;
  private final DatesBlockValidationService datesBlockValidationService;

  public RaidoStableV2ValidationService(
    final TitleValidationService titleSvc,
    final DescriptionValidationService descSvc,
    final ContributorValidationService contribSvc,
    final OrganisationValidationService orgSvc,
    final SubjectValidationService subjectSvc,
    final RelatedObjectValidationService relatedObjectSvc,
    final AlternateIdentifierValidationService alternateIdentifierSvc,
    final RelatedRaidValidationService relatedRaidSvc,
    final SpatialCoverageValidationService spatialCoverageSvc,
    final TraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelSvc,
    final AccessBlockValidationService accessBlockValidationService,
    final AlternateUrlBlockValidationService alternateUrlBlockValidationService,
    final IdBlockV2ValidationService idBlockV2ValidationService, final DatesBlockValidationService datesBlockValidationService) {
    this.titleSvc = titleSvc;
    this.descSvc = descSvc;
    this.contribSvc = contribSvc;
    this.orgSvc = orgSvc;
    this.subjectSvc = subjectSvc;
    this.idBlockV2ValidationService = idBlockV2ValidationService;
    this.relatedObjectSvc = relatedObjectSvc;
    this.alternateIdentifierSvc = alternateIdentifierSvc;
    this.relatedRaidSvc = relatedRaidSvc;
    this.spatialCoverageSvc = spatialCoverageSvc;
    this.traditionalKnowledgeLabelSvc = traditionalKnowledgeLabelSvc;
    this.accessBlockValidationService = accessBlockValidationService;
    this.alternateUrlBlockValidationService = alternateUrlBlockValidationService;
    this.datesBlockValidationService = datesBlockValidationService;
  }

  public List<ValidationFailure> validateForUpdate(final String handle, final UpdateRaidStableV2Request request) {
    if( request == null ){
      return of(ValidationMessage.METADATA_NOT_SET);
    }

    String decodedHandle = urlDecode(handle);

    final var failures = new ArrayList<>(idBlockV2ValidationService.validateUpdateHandle(decodedHandle, request.getId()));

    if (request.getSchemaVersion() == null) {
      failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
    }

    failures.addAll(datesBlockValidationService.validateDates(request.getDates()));
    failures.addAll(accessBlockValidationService.validateAccess(request.getAccess()));
    failures.addAll(titleSvc.validateTitles(request.getTitles()));
    failures.addAll(descSvc.validateDescriptions(request.getDescriptions()));
    failures.addAll(alternateUrlBlockValidationService.validateAlternateUrls(request.getAlternateUrls()));
    failures.addAll(contribSvc.validateContributors(request.getContributors()));
    failures.addAll(orgSvc.validateOrganisations(request.getOrganisations()));
    failures.addAll(subjectSvc.validateSubjects(request.getSubjects()));
    failures.addAll(relatedRaidSvc.validateRelatedRaids(request.getRelatedRaids()));
    failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObjects()));
    failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(request.getAlternateIdentifiers()));
    failures.addAll(spatialCoverageSvc.validateSpatialCoverages(request.getSpatialCoverages()));
    failures.addAll(traditionalKnowledgeLabelSvc.validateTraditionalKnowledgeLabels(
      request.getTraditionalKnowledgeLabels()));

    return failures;
  }
}
