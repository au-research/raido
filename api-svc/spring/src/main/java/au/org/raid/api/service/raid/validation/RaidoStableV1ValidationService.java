package au.org.raid.api.service.raid.validation;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.util.Log;
import au.org.raid.api.validator.*;
import au.org.raid.idl.raidv2.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.HANDLE_DOES_NOT_MATCH_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.handlesDoNotMatch;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.RestUtil.urlDecode;
import static au.org.raid.api.util.StringUtil.areDifferent;
import static au.org.raid.api.util.StringUtil.isBlank;
import static java.util.List.of;

@Component
public class RaidoStableV1ValidationService {
    private static final Log log = to(RaidoStableV1ValidationService.class);

    private final TitleValidator titleSvc;
    private final DescriptionValidator descSvc;
    private final ContributorValidator contribSvc;
    private final OrganisationValidator orgSvc;
    private final AccessValidator accessValidationService;
    private final SubjectValidator subjectSvc;
    private final IdentifierParser idParser;
    private final RelatedObjectValidator relatedObjectSvc;
    private final AlternateIdentifierValidator alternateIdentifierSvc;
    private final RelatedRaidValidator relatedRaidSvc;
    private final SpatialCoverageValidator spatialCoverageSvc;
    private final TraditionalKnowledgeLabelValidator traditionalKnowledgeLabelSvc;

    public RaidoStableV1ValidationService(
            final TitleValidator titleSvc,
            final DescriptionValidator descSvc,
            final ContributorValidator contribSvc,
            final OrganisationValidator orgSvc,
            final SubjectValidator subjectSvc,
            final IdentifierParser idParser,
            final RelatedObjectValidator relatedObjectSvc,
            final AlternateIdentifierValidator alternateIdentifierSvc,
            final RelatedRaidValidator relatedRaidSvc,
            final SpatialCoverageValidator spatialCoverageSvc,
            final TraditionalKnowledgeLabelValidator traditionalKnowledgeLabelSvc,
            final AccessValidator accessValidationService) {
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

    private static List<ValidationFailure> validate(
            Dates dates
    ) {
        var failures = new ArrayList<ValidationFailure>();
        if (dates == null) {
            failures.add(ValidationMessage.DATES_NOT_SET);
        } else {
            if (dates.getStartDate() == null) {
                failures.add(ValidationMessage.DATES_START_DATE_NOT_SET);
            }
        }
        return failures;
    }

    private List<ValidationFailure> validateUpdateHandle(final String decodedHandleFromPath, final Id id) {
        final var failures = new ArrayList<ValidationFailure>();

        IdentifierUrl updateId = null;
        try {
            updateId = idParser.parseUrlWithException(id.getIdentifier());
        } catch (ValidationFailureException e) {
            failures.addAll(e.getFailures());
        }

        IdentifierHandle pathHandle = null;
        try {
            pathHandle = idParser.parseHandleWithException(decodedHandleFromPath);
        } catch (ValidationFailureException e) {
            failures.addAll(e.getFailures());
        }

        if (updateId != null && pathHandle != null) {
            if (areDifferent(pathHandle.format(), updateId.handle().format())) {
                log.with("pathHandle", pathHandle.format()).
                        with("updateId", updateId.handle().format()).
                        error(HANDLE_DOES_NOT_MATCH_MESSAGE);
                failures.add(handlesDoNotMatch());
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
        if (urls == null) {
            return Collections.emptyList();
        }

        var failures = new ArrayList<ValidationFailure>();

        for (int i = 0; i < urls.size(); i++) {
            var iUrl = urls.get(i);

            if (isBlank(iUrl.getUrl())) {
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
        if (request == null) {
            return of(ValidationMessage.METADATA_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();

        failures.addAll(validate(request.getDates()));
        failures.addAll(accessValidationService.validate(request.getAccess()));
        failures.addAll(titleSvc.validate(request.getTitles()));
        failures.addAll(descSvc.validate(request.getDescriptions()));
        failures.addAll(validateAlternateUrls(request.getAlternateUrls()));
        failures.addAll(contribSvc.validate(request.getContributors()));
        failures.addAll(orgSvc.validate(request.getOrganisations()));
        failures.addAll(subjectSvc.validate(request.getSubjects()));
        failures.addAll(relatedRaidSvc.validate(request.getRelatedRaids()));
        failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObjects()));
        failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(request.getAlternateIdentifiers()));
        failures.addAll(spatialCoverageSvc.validate(request.getSpatialCoverages()));
        failures.addAll(traditionalKnowledgeLabelSvc.validate(
                request.getTraditionalKnowledgeLabels()));

        return failures;
    }

    public List<ValidationFailure> validateForUpdate(final String handle, final UpdateRaidV1Request request) {
        if (request == null) {
            return of(ValidationMessage.METADATA_NOT_SET);
        }

        String decodedHandle = urlDecode(handle);

        final var failures = new ArrayList<>(validateUpdateHandle(decodedHandle, request.getId()));

        failures.addAll(validate(request.getDates()));
        failures.addAll(accessValidationService.validate(request.getAccess()));
        failures.addAll(titleSvc.validate(request.getTitles()));
        failures.addAll(descSvc.validate(request.getDescriptions()));
        failures.addAll(validateAlternateUrls(request.getAlternateUrls()));
        failures.addAll(contribSvc.validate(request.getContributors()));
        failures.addAll(orgSvc.validate(request.getOrganisations()));
        failures.addAll(subjectSvc.validate(request.getSubjects()));
        failures.addAll(relatedRaidSvc.validate(request.getRelatedRaids()));
        failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObjects()));
        failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(request.getAlternateIdentifiers()));
        failures.addAll(spatialCoverageSvc.validate(request.getSpatialCoverages()));
        failures.addAll(traditionalKnowledgeLabelSvc.validate(
                request.getTraditionalKnowledgeLabels()));

        return failures;
    }
}