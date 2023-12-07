package au.org.raid.api.service;

import au.org.raid.api.factory.record.OrganisationRecordFactory;
import au.org.raid.api.factory.record.RaidOrganisationRecordFactory;
import au.org.raid.api.repository.OrganisationRepository;
import au.org.raid.api.repository.OrganisationSchemaRepository;
import au.org.raid.api.repository.RaidOrganisationRepository;
import au.org.raid.idl.raidv2.model.Organisation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganisationService {
    private final OrganisationRepository organisationRepository;
    private final OrganisationSchemaRepository organisationSchemaRepository;
    private final RaidOrganisationRepository raidOrganisationRepository;
    private final OrganisationRecordFactory organisationRecordFactory;
    private final RaidOrganisationRecordFactory raidOrganisationRecordFactory;
    private final OrganisationRoleService organisationRoleService;

    public void create(final Organisation organisation, final String raidName) {

        final var organisationSchemaRecord = organisationSchemaRepository.findByUri(organisation.getSchemaUri())
                .orElseThrow(() ->
                        new RuntimeException("Contributor role schema not found %s".formatted(organisation.getSchemaUri())));

        final var organisationRecord = organisationRecordFactory.create(organisation, organisationSchemaRecord.getId());
        final var savedOrganisation = organisationRepository.create(organisationRecord);
        final var raidOrganisationRecord = raidOrganisationRecordFactory.create(savedOrganisation.getId(), raidName);

        final var savedRaidOrganisationRecord = raidOrganisationRepository.create(raidOrganisationRecord);

        for (final var role : organisation.getRole()) {
            organisationRoleService.create(role, savedRaidOrganisationRecord.getId());
        }
    }
}
