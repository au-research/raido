package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidOrganisationRecordFactory;
import au.org.raid.api.factory.record.RaidOrganisationRoleRecordFactory;
import au.org.raid.api.repository.OrganisationRoleRepository;
import au.org.raid.api.repository.OrganisationRoleSchemaRepository;
import au.org.raid.api.repository.RaidOrganisationRoleRepository;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganisationRoleService {
    private final OrganisationRoleRepository organisationRoleRepository;
    private final OrganisationRoleSchemaRepository organisationRoleSchemaRepository;
    private final RaidOrganisationRoleRepository raidOrganisationRoleRepository;
    private final RaidOrganisationRoleRecordFactory raidOrganisationRoleRecordFactory;

    public void create(final OrganisationRoleWithSchemaUri organisationRole, final int raidOrganisationId) {
        final var organisationRoleSchemaRecord =
                organisationRoleSchemaRepository.findByUri(organisationRole.getSchemaUri())
                        .orElseThrow(() -> new RuntimeException("Organisation role schema not found %s".formatted(organisationRole.getSchemaUri())));

        final var organisationRoleRecord = organisationRoleRepository.findByUriAndSchemaId(organisationRole.getId(), organisationRoleSchemaRecord.getId())
                .orElseThrow(() ->
                        new RuntimeException("Organisation role not found %s with schema %s".formatted(organisationRole.getId(), organisationRole.getSchemaUri())));

        final var raidOrganisationRoleRecord = raidOrganisationRoleRecordFactory.create(
                raidOrganisationId,
                organisationRoleRecord.getId(),
                organisationRole.getStartDate(),
                organisationRole.getEndDate()
        );

        raidOrganisationRoleRepository.create(raidOrganisationRoleRecord);
    }
}
