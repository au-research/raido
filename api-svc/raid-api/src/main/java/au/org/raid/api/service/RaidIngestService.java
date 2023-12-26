package au.org.raid.api.service;

import au.org.raid.api.factory.DateFactory;
import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.RaidRecordFactory;
import au.org.raid.api.repository.RaidRepository;
import au.org.raid.idl.raidv2.model.RaidDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    private final IdService idService;
    private final DateFactory dateFactory;
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

    public Optional<RaidDto> findByHandle(final String handle) {
        final var raidRecordOptional = raidRepository.findByHandle(handle);

        if (raidRecordOptional.isEmpty()) {
            return Optional.empty();
        }

        final var record = raidRecordOptional.get();

        final var titles = titleService.findAllByHandle(handle);
        final var descriptions = descriptionService.findAllByHandle(handle);
        final var contributors = contributorService.findAllByHandle(handle);
        final var organisations = organisationService.findAllByHandle(handle);
        final var relatedObjects = relatedObjectService.findAllByHandle(handle);
        final var alternateIdentifiers = alternateIdentifierService.findAllByHandle(handle);
        final var alternateUrls = alternateUrlService.findAllByHandle(handle);
        final var relatedRaids = relatedRaidService.findAllByHandle(handle);
        final var subjects = subjectService.findAllByHandle(handle);
        final var traditionalKnowledgeLabels = traditionalKnowledgeLabelService.findAllByHandle(handle);
        final var spatialCoverages = spatialCoverageService.findAllByHandle(handle);

        return Optional.of(new RaidDto()
                .identifier(idService.getId(record))
                .date(dateFactory.create(record.getStartDateString(), record.getEndDate()))
                .title(titles.isEmpty() ? null : titles)
                .description(descriptions.isEmpty() ? null : descriptions)
                .contributor(contributors.isEmpty() ? null : contributors)
                .organisation(organisations.isEmpty() ? null: organisations)
                .relatedObject(relatedObjects.isEmpty() ? null : relatedObjects)
                .alternateIdentifier(alternateIdentifiers.isEmpty() ? null : alternateIdentifiers)
                .alternateUrl(alternateUrls.isEmpty() ? null : alternateUrls)
                .relatedRaid(relatedRaids.isEmpty() ? null : relatedRaids)
                .access(accessService.getAccess(record))
                .subject(subjects.isEmpty() ? null : subjects)
                .traditionalKnowledgeLabel(traditionalKnowledgeLabels.isEmpty() ? null : traditionalKnowledgeLabels)
                .spatialCoverage(spatialCoverages.isEmpty() ? null : spatialCoverages));
    }

    public Optional<RaidDto> update(final RaidDto raid) {
        final var handle = new Handle(raid.getIdentifier().getId()).toString();

        if (raidRepository.findByHandle(handle).isEmpty()) {
            return Optional.empty();
        }

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

        raidRepository.update(raidRecord);

        titleService.update(raid.getTitle(), handle);

        descriptionService.update(raid.getDescription(), handle);
        contributorService.update(raid.getContributor(), handle);
        organisationService.update(raid.getOrganisation(), handle);
        relatedObjectService.update(raid.getRelatedObject(), handle);
        alternateIdentifierService.update(raid.getAlternateIdentifier(), handle);
        alternateUrlService.update(raid.getAlternateUrl(), handle);
        relatedRaidService.update(raid.getRelatedRaid(), handle);
        subjectService.update(raid.getSubject(), handle);
        traditionalKnowledgeLabelService.update(raid.getTraditionalKnowledgeLabel(), handle);
        spatialCoverageService.update(raid.getSpatialCoverage(), handle);

        return Optional.of(raid);
    }
}
