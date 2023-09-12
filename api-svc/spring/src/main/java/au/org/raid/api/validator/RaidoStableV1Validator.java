package au.org.raid.api.validator;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.*;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RaidoStableV1Validator {
    private static final Log log = to(RaidoStableV1Validator.class);

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
    private final DateValidator dateValidator;

    private List<ValidationFailure> validateUpdateHandle(final String decodedHandleFromPath, final Id id) {
        final var failures = new ArrayList<ValidationFailure>();

        IdentifierUrl updateId = null;
        try {
            updateId = idParser.parseUrlWithException(id.getId());
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

        failures.addAll(dateValidator.validate(request.getDate()));
        failures.addAll(accessValidationService.validate(request.getAccess()));
        failures.addAll(titleSvc.validate(request.getTitle()));
        failures.addAll(descSvc.validate(request.getDescription()));
        failures.addAll(validateAlternateUrls(request.getAlternateUrl()));
        failures.addAll(contribSvc.validate(request.getContributor()));
        failures.addAll(orgSvc.validate(request.getOrganisation()));
        failures.addAll(subjectSvc.validate(request.getSubject()));
        failures.addAll(relatedRaidSvc.validate(request.getRelatedRaid()));
        failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObject()));
        failures.addAll(alternateIdentifierSvc.validateAlternateIdentifier(request.getAlternateIdentifier()));
        failures.addAll(spatialCoverageSvc.validate(request.getSpatialCoverage()));
        failures.addAll(traditionalKnowledgeLabelSvc.validate(
                request.getTraditionalKnowledgeLabel()));

        return failures;
    }

    public List<ValidationFailure> validateForUpdate(final String handle, final UpdateRaidV1Request request) {
        if (request == null) {
            return of(ValidationMessage.METADATA_NOT_SET);
        }

        String decodedHandle = urlDecode(handle);

        final var failures = new ArrayList<>(validateUpdateHandle(decodedHandle, request.getIdentifier()));

        failures.addAll(dateValidator.validate(request.getDate()));
        failures.addAll(accessValidationService.validate(request.getAccess()));
        failures.addAll(titleSvc.validate(request.getTitle()));
        failures.addAll(descSvc.validate(request.getDescription()));
        failures.addAll(validateAlternateUrls(request.getAlternateUrl()));
        failures.addAll(contribSvc.validate(request.getContributor()));
        failures.addAll(orgSvc.validate(request.getOrganisation()));
        failures.addAll(subjectSvc.validate(request.getSubject()));
        failures.addAll(relatedRaidSvc.validate(request.getRelatedRaid()));
        failures.addAll(relatedObjectSvc.validateRelatedObjects(request.getRelatedObject()));
        failures.addAll(alternateIdentifierSvc.validateAlternateIdentifier(request.getAlternateIdentifier()));
        failures.addAll(spatialCoverageSvc.validate(request.getSpatialCoverage()));
        failures.addAll(traditionalKnowledgeLabelSvc.validate(
                request.getTraditionalKnowledgeLabel()));

        return failures;
    }
}