package au.org.raid.api.service.raid.validation;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.fieldCannotChange;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.areEqual;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static java.util.List.of;

@Component
public class RaidoSchemaV1ValidationService {
    private static final Log log = to(RaidoSchemaV1ValidationService.class);

    private final TitleValidationService titleSvc;
    private final DescriptionValidationService descSvc;
    private final ContributorValidationService contribSvc;
    private final OrganisationValidationService orgSvc;
    private final SubjectValidationService subjectSvc;
    private final RelatedRaidValidationService relatedRaidSvc;
    private final IdentifierParser handleParser;
    private final RelatedObjectValidationService relatedObjectSvc;
    private final AlternateIdentifierValidationService alternateIdentifierSvc;
    private final SpatialCoverageValidationService spatialCoverageSvc;
    private final TraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelValidatorSvc;

    public RaidoSchemaV1ValidationService(
            final TitleValidationService titleSvc,
            final DescriptionValidationService descSvc,
            final ContributorValidationService contribSvc,
            final OrganisationValidationService orgSvc,
            final SubjectValidationService subjectSvc,
            final RelatedRaidValidationService relatedRaidSvc,
            final IdentifierParser handleParser,
            final RelatedObjectValidationService relatedObjectSvc, final AlternateIdentifierValidationService alternateIdentifierSvc, final SpatialCoverageValidationService spatialCoverageSvc, final TraditionalKnowledgeLabelValidatorService traditionalKnowledgeLabelValidatorSvc) {
        this.titleSvc = titleSvc;
        this.descSvc = descSvc;
        this.contribSvc = contribSvc;
        this.orgSvc = orgSvc;
        this.subjectSvc = subjectSvc;
        this.relatedRaidSvc = relatedRaidSvc;
        this.handleParser = handleParser;
        this.relatedObjectSvc = relatedObjectSvc;
        this.alternateIdentifierSvc = alternateIdentifierSvc;
        this.spatialCoverageSvc = spatialCoverageSvc;
        this.traditionalKnowledgeLabelValidatorSvc = traditionalKnowledgeLabelValidatorSvc;
    }

    private static List<ValidationFailure> validateDates(
            DatesBlock dates
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

    private static List<ValidationFailure> validateAccess(
            AccessBlock access
    ) {
        var failures = new ArrayList<ValidationFailure>();

        if (access == null) {
            failures.add(ValidationMessage.ACCESS_NOT_SET);
        } else {
            if (access.getType() == null) {
                failures.add(ValidationMessage.ACCESS_TYPE_NOT_SET);
            } else {
                if (
                        access.getType() == AccessType.CLOSED &&
                                access.getAccessStatement() == null
                ) {
                    failures.add(ValidationMessage.ACCESS_STATEMENT_NOT_SET);
                }
            }
        }

        return failures;
    }

    /**
     * Does not currently validate the ID block.
     */
    public List<ValidationFailure> validateRaidoSchemaV1(
            RaidoMetadataSchemaV1 metadata
    ) {
        if (metadata == null) {
            return of(ValidationMessage.METADATA_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();
        if (metadata.getMetadataSchema() != RAIDOMETADATASCHEMAV1) {
            failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
        }

        failures.addAll(validateDates(metadata.getDates()));
        failures.addAll(validateAccess(metadata.getAccess()));
        failures.addAll(titleSvc.validateTitles(metadata.getTitles()));
        failures.addAll(descSvc.validateDescriptions(metadata.getDescriptions()));
        failures.addAll(validateAlternateUrls(metadata.getAlternateUrls()));
        failures.addAll(contribSvc.validateContributors(metadata.getContributors()));
        failures.addAll(orgSvc.validateOrganisations(metadata.getOrganisations()));
        failures.addAll(subjectSvc.validateSubjects(metadata.getSubjects()));
        failures.addAll(relatedRaidSvc.validateRelatedRaids(metadata.getRelatedRaids()));
        failures.addAll(relatedObjectSvc.validateRelatedObjects(metadata.getRelatedObjects()));
        failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(metadata.getAlternateIdentifiers()));
        failures.addAll(spatialCoverageSvc.validateSpatialCoverages(metadata.getSpatialCoverages()));
        failures.addAll(traditionalKnowledgeLabelValidatorSvc.validateTraditionalKnowledgeLabels(
                metadata.getTraditionalKnowledgeLabels()));

        return failures;
    }

    /**
     * Does not currently validate the ID block.
     */
    public List<ValidationFailure> validateRaidoSchemaV2(
            RaidoMetadataSchemaV2 metadata
    ) {
        if (metadata == null) {
            return of(ValidationMessage.METADATA_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();
        if (metadata.getMetadataSchema() != au.org.raid.idl.raidv2.model.RaidoMetaschemaV2.RAIDOMETADATASCHEMAV2) {
            failures.add(ValidationMessage.INVALID_METADATA_SCHEMA);
        }

        failures.addAll(validateDates(metadata.getDates()));
        failures.addAll(validateAccess(metadata.getAccess()));
        failures.addAll(titleSvc.validateTitles(metadata.getTitles()));
        failures.addAll(descSvc.validateDescriptions(metadata.getDescriptions()));
        failures.addAll(validateAlternateUrls(metadata.getAlternateUrls()));
        failures.addAll(contribSvc.validateContributors(metadata.getContributors()));
        failures.addAll(orgSvc.validateOrganisations(metadata.getOrganisations()));
        failures.addAll(subjectSvc.validateSubjects(metadata.getSubjects()));
        failures.addAll(relatedRaidSvc.validateRelatedRaids(metadata.getRelatedRaids()));
        failures.addAll(relatedObjectSvc.validateRelatedObjects(metadata.getRelatedObjects()));
        failures.addAll(alternateIdentifierSvc.validateAlternateIdentifiers(metadata.getAlternateIdentifiers()));
        failures.addAll(spatialCoverageSvc.validateSpatialCoverages(metadata.getSpatialCoverages()));
        failures.addAll(traditionalKnowledgeLabelValidatorSvc.validateTraditionalKnowledgeLabels(
                metadata.getTraditionalKnowledgeLabels()));

        return failures;
    }

    public List<ValidationFailure> validateLegacySchemaV1(
            LegacyMetadataSchemaV1 metadata
    ) {
        if (metadata == null) {
            return of(ValidationMessage.METADATA_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();
        if (metadata.getMetadataSchema() != LEGACYMETADATASCHEMAV1) {
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

    public List<ValidationFailure> validateIdBlockForMigration(IdBlock id) {
        var failures = new ArrayList<ValidationFailure>();
        if (id == null) {
            failures.add(ValidationMessage.ID_BLOCK_NOT_SET);
            return failures;
        }

        if (isBlank(id.getIdentifier())) {
            failures.add(ValidationMessage.IDENTIFIER_NOT_SET);
        } else {
            var parseResult = handleParser.parseUrl(id.getIdentifier());
            if (parseResult instanceof IdentifierParser.ParseProblems idProblems) {
                log.with("handle", id.getIdentifier()).
                        with("problems", idProblems.getProblems()).
                        warn("migration identifier could not be parsed");
                failures.addAll(IdentifierParser.mapProblemsToValidationFailures(idProblems));
            }
        }

        // improve: would be good to validate the handle format
        // at least that prefix and suffix exist, separated by slash

        return failures;
    }

    public List<ValidationFailure> validateIdBlockNotChanged(
            IdBlock newId,
            IdBlock oldId
    ) {
        List<ValidationFailure> failures = new ArrayList<>();

        if (!areEqual(newId.getIdentifier(), oldId.getIdentifier())) {
            failures.add(fieldCannotChange("metadata.id.identifier"));
        }

        return failures;
    }

    public List<ValidationFailure> validateAlternateUrls(
            List<AlternateUrlBlock> urls
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
}