package au.org.raid.api.service;

import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.RaidRecordFactory;
import au.org.raid.api.repository.RaidRepository;
import au.org.raid.idl.raidv2.model.RaidDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RaidIngestService {
    private final TitleService titleService;
    private final DescriptionService descriptionService;
    private final ContributorService contributorService;
    private final OrganisationService organisationService;
    private final RelatedObjectService relatedObjectService;
    private final AlternateIdentifierService alternateIdentifierService;
    private final AlternateUrlService alternateUrlService;
    private final RelatedRaidService relatedRaidService;
    private final SubjectService subjectService;
    private final TraditionalKnowledgeLabelService traditionalKnowledgeLabelService;
    private final SpatialCoverageService spatialCoverageService;
    private final RaidRepository raidRepository;
    private final RaidRecordFactory raidRecordFactory;
    private final AccessService accessService;
    private final LanguageService languageService;
    private final HandleFactory handleFactory;

    public void create(final RaidDto raid) {
        final var handle = handleFactory.create(raid.getIdentifier().getId()).toString();

        final var accessTypeId = accessService.findAccessTypeId(raid.getAccess());

        Integer accessStatementLanguageId = null;
        if (raid.getAccess().getStatement() != null) {
            accessStatementLanguageId = languageService.findLanguageId(raid.getAccess().getStatement().getLanguage());
        }

        final Integer registrationAgencyOrganisationId = organisationService.findOrCreate(
                raid.getIdentifier().getRegistrationAgency().getId(),
                raid.getIdentifier().getRegistrationAgency().getSchemaUri()
        );

        final Integer ownerOrganisationId = organisationService.findOrCreate(
                raid.getIdentifier().getOwner().getId(),
                raid.getIdentifier().getOwner().getSchemaUri()
        );

        final var raidRecord = raidRecordFactory.create(raid, accessTypeId, accessStatementLanguageId,
                registrationAgencyOrganisationId, ownerOrganisationId);

        raidRepository.insert(raidRecord);

        titleService.create(raid.getTitle(), handle);
        descriptionService.create(raid.getDescription(), handle);
        contributorService.create(raid.getContributor(), handle);
        organisationService.create(raid.getOrganisation(), handle);
        relatedObjectService.create(raid.getRelatedObject(), handle);
        alternateIdentifierService.create(raid.getAlternateIdentifier(), handle);
        alternateUrlService.create(raid.getAlternateUrl(), handle);
        relatedRaidService.create(raid.getRelatedRaid(), handle);
        subjectService.create(raid.getSubject(), handle);
        traditionalKnowledgeLabelService.create(raid.getTraditionalKnowledgeLabel(), handle);
        spatialCoverageService.create(raid.getSpatialCoverage(), handle);
    }
}
