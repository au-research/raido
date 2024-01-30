package au.org.raid.api.service;

import au.org.raid.api.factory.DateFactory;
import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.RaidRecordFactory;
import au.org.raid.api.repository.RaidRepository;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static au.org.raid.api.util.TestRaid.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaidIngestServiceTest {
    @Mock
    private TitleService titleService;
    @Mock
    private DescriptionService descriptionService;
    @Mock
    private ContributorService contributorService;
    @Mock
    private OrganisationService organisationService;
    @Mock
    private RelatedObjectService relatedObjectService;
    @Mock
    private AlternateIdentifierService alternateIdentifierService;
    @Mock
    private AlternateUrlService alternateUrlService;
    @Mock
    private RelatedRaidService relatedRaidService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private TraditionalKnowledgeLabelService traditionalKnowledgeLabelService;
    @Mock
    private SpatialCoverageService spatialCoverageService;
    @Mock
    private RaidRepository raidRepository;
    @Mock
    private RaidRecordFactory raidRecordFactory;
    @Mock
    private AccessService accessService;
    @Mock
    private LanguageService languageService;
    @Mock
    private HandleFactory handleFactory;
    @Mock
    private IdService idService;
    @Mock
    private DateFactory dateFactory;
    @Mock
    private CacheableRaidService cacheableRaidService;
    @InjectMocks
    private RaidIngestService raidIngestService;

    @Test
    @DisplayName("create() saves raid and relations")
    void create() {
        final var handle = new Handle(RAID_DTO.getIdentifier().getId());
        final var accessTypeId = 123;
        final var accessStatementLanguageId = 234;
        final var registrationAgencyOrganisationId = 345;
        final var ownerOrganisationId = 456;

        final var raidRecord = new RaidRecord();

        when(handleFactory.create(RAID_DTO.getIdentifier().getId())).thenReturn(handle);
        when(accessService.findAccessTypeId(RAID_DTO.getAccess())).thenReturn(accessTypeId);
        when(languageService.findLanguageId(RAID_DTO.getAccess().getStatement().getLanguage()))
                .thenReturn(accessStatementLanguageId);
        when(organisationService.findOrCreate(REGISTRATION_AGENCY_ID, ROR_SCHEMA_URI))
                .thenReturn(registrationAgencyOrganisationId);
        when(organisationService.findOrCreate(OWNER_ID, ROR_SCHEMA_URI))
                .thenReturn(ownerOrganisationId);

        when(raidRecordFactory.create(RAID_DTO, accessTypeId, accessStatementLanguageId, registrationAgencyOrganisationId, ownerOrganisationId))
                .thenReturn(raidRecord);

        raidIngestService.create(RAID_DTO);

        verify(raidRepository).insert(raidRecord);
        verify(titleService).create(RAID_DTO.getTitle(), HANDLE);
        verify(descriptionService).create(RAID_DTO.getDescription(), HANDLE);
        verify(contributorService).create(RAID_DTO.getContributor(), HANDLE);
        verify(organisationService).create(RAID_DTO.getOrganisation(), HANDLE);
        verify(relatedObjectService).create(RAID_DTO.getRelatedObject(), HANDLE);
        verify(alternateIdentifierService).create(RAID_DTO.getAlternateIdentifier(), HANDLE);
        verify(alternateUrlService).create(RAID_DTO.getAlternateUrl(), HANDLE);
        verify(relatedRaidService).create(RAID_DTO.getRelatedRaid(), HANDLE);
        verify(subjectService).create(RAID_DTO.getSubject(), HANDLE);
        verify(traditionalKnowledgeLabelService).create(RAID_DTO.getTraditionalKnowledgeLabel(), HANDLE);
        verify(spatialCoverageService).create(RAID_DTO.getSpatialCoverage(), HANDLE);
    }

    @Test
    @DisplayName("findAllByServicePointIdOrNotConfidential()")
    void findAllByServicePointIdOrNotConfidential() {
        final var servicePointId = 123L;

        final var apiToken = mock(ApiToken.class);
        when(apiToken.getServicePointId()).thenReturn(servicePointId);

        final var raidRecord = new RaidRecord()
                .setHandle(HANDLE)
                .setStartDateString(START_DATE)
                .setEndDate(END_DATE);

        when(raidRepository.findAllByServicePointIdOrNotConfidential(servicePointId)).thenReturn(List.of(raidRecord));
        when(cacheableRaidService.build(raidRecord)).thenReturn(RAID_DTO);

        final var result = raidIngestService.findAllByServicePointIdOrNotConfidential(apiToken);

        assertThat(result, is(List.of(RAID_DTO)));
    }

    @Test
    @DisplayName("findAllByServicePointId()")
    void findAllByServicePointId() {
        final var servicePointId = 123L;

        final var raidRecord = new RaidRecord()
                .setHandle(HANDLE)
                .setStartDateString(START_DATE)
                .setEndDate(END_DATE);

        when(raidRepository.findAllByServicePointId(servicePointId)).thenReturn(List.of(raidRecord));
        when(cacheableRaidService.build(raidRecord)).thenReturn(RAID_DTO);

        final var result = raidIngestService.findAllByServicePointId(servicePointId);

        assertThat(result, is(List.of(RAID_DTO)));
    }

    @Test
    @DisplayName("findByHandle() returns raid from handle")
    void findByHandle() {
        final var raidRecord = new RaidRecord()
                .setHandle(HANDLE)
                .setStartDateString(START_DATE)
                .setEndDate(END_DATE);

        when(raidRepository.findByHandle(HANDLE)).thenReturn(Optional.of(raidRecord));
        when(cacheableRaidService.build(raidRecord)).thenReturn(RAID_DTO);

        assertThat(raidIngestService.findByHandle(HANDLE), is(Optional.of(RAID_DTO)));
    }

    @Test
    @DisplayName("findByHandle() returns empty Optional if none found")
    void findByHandleReturnsEmptyOptional() {
        when(raidRepository.findByHandle(HANDLE)).thenReturn(Optional.empty());
        assertThat(raidIngestService.findByHandle(HANDLE), is(Optional.empty()));
    }

    private void mockBuild(final RaidRecord record) {
        when(titleService.findAllByHandle(HANDLE)).thenReturn(TITLES);
        when(descriptionService.findAllByHandle(HANDLE)).thenReturn(DESCRIPTIONS);
        when(contributorService.findAllByHandle(HANDLE)).thenReturn(CONTRIBUTORS);
        when(organisationService.findAllByHandle(HANDLE)).thenReturn(ORGANISATIONS);
        when(relatedObjectService.findAllByHandle(HANDLE)).thenReturn(RELATED_OBJECTS);
        when(alternateIdentifierService.findAllByHandle(HANDLE)).thenReturn(ALTERNATE_IDENTIFIERS);
        when(alternateUrlService.findAllByHandle(HANDLE)).thenReturn(ALTERNATE_URLS);
        when(relatedRaidService.findAllByHandle(HANDLE)).thenReturn(RELATED_RAIDS);
        when(subjectService.findAllByHandle(HANDLE)).thenReturn(SUBJECTS);
        when(traditionalKnowledgeLabelService.findAllByHandle(HANDLE)).thenReturn(TRADITIONAL_KNOWLEDGE_LABELS);
        when(spatialCoverageService.findAllByHandle(HANDLE)).thenReturn(SPATIAL_COVERAGES);

        when(idService.getId(record)).thenReturn(IDENTIFIER);
        when(dateFactory.create(START_DATE, END_DATE)).thenReturn(DATES);
        when(accessService.getAccess(record)).thenReturn(ACCESS);
    }

    @Test
    @DisplayName("update() updates raid and dependencies")
    void update() {
        final var accessTypeId = 123;
        final var accessStatementLanguageId = 234;
        final var registrationAgencyOrganisationId = 345;
        final var ownerOrganisationId = 345;

        final var handle = new Handle(RAID_DTO.getIdentifier().getId());
        final var raidRecord = new RaidRecord();

        when(handleFactory.create(RAID_DTO.getIdentifier().getId())).thenReturn(handle);

        when(raidRepository.findByHandle(HANDLE)).thenReturn(Optional.of(raidRecord));

        when(accessService.findAccessTypeId(ACCESS)).thenReturn(accessTypeId);
        when(languageService.findLanguageId(LANGUAGE)).thenReturn(accessStatementLanguageId);
        when(organisationService.findOrCreate(REGISTRATION_AGENCY_ID, ROR_SCHEMA_URI))
                .thenReturn(registrationAgencyOrganisationId);
        when(organisationService.findOrCreate(OWNER_ID, ROR_SCHEMA_URI))
                .thenReturn(ownerOrganisationId);

        when(raidRecordFactory.create(RAID_DTO, accessTypeId, accessStatementLanguageId,
                registrationAgencyOrganisationId, ownerOrganisationId)).thenReturn(raidRecord);

        assertThat(raidIngestService.update(RAID_DTO), is(RAID_DTO));

        verify(raidRepository).update(raidRecord);
        verify(titleService).update(TITLES, HANDLE);
        verify(descriptionService).update(DESCRIPTIONS, HANDLE);
        verify(contributorService).update(CONTRIBUTORS, HANDLE);
        verify(organisationService).update(ORGANISATIONS, HANDLE);
        verify(relatedObjectService).update(RELATED_OBJECTS, HANDLE);
        verify(alternateIdentifierService).update(ALTERNATE_IDENTIFIERS, HANDLE);
        verify(alternateUrlService).update(ALTERNATE_URLS, HANDLE);
        verify(relatedRaidService).update(RELATED_RAIDS, HANDLE);
        verify(subjectService).update(SUBJECTS, HANDLE);
        verify(traditionalKnowledgeLabelService).update(TRADITIONAL_KNOWLEDGE_LABELS, HANDLE);
        verify(spatialCoverageService).update(SPATIAL_COVERAGES, HANDLE);
    }
}