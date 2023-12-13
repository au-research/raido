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

import java.util.List;

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

    public void create(final List<Organisation> organisations, final String handle) {
        if (organisations == null) {
            return;
        }

        for (final var organisation : organisations) {
            final var organisationSchemaRecord = organisationSchemaRepository.findByUri(organisation.getSchemaUri())
                    .orElseThrow(() ->
                            new RuntimeException("Contributor role schema not found %s".formatted(organisation.getSchemaUri())));

            final var organisationRecord = organisationRecordFactory.create(organisation, organisationSchemaRecord.getId());
            final var savedOrganisation = organisationRepository.findOrCreate(organisationRecord);
            final var raidOrganisationRecord = raidOrganisationRecordFactory.create(savedOrganisation.getId(), handle);

            final var savedRaidOrganisationRecord = raidOrganisationRepository.create(raidOrganisationRecord);

            for (final var role : organisation.getRole()) {
                organisationRoleService.create(role, savedRaidOrganisationRecord.getId());
            }
        }
    }

    public Integer findOrganisationId(final String organisationUri, final String schemaUri) {
        final var organisationSchemaRecord = organisationSchemaRepository.findByUri(schemaUri)
                .orElseThrow(() -> new RuntimeException("Organisation schema not found %s".formatted(schemaUri)));

        final var organisationRecord = organisationRepository.findByUriAndSchemaId(
                        organisationUri, organisationSchemaRecord.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Organisation %s not found in schema %s".formatted(organisationUri, schemaUri)));

        return organisationRecord.getId();
    }

    public Integer findOrCreate(final String pid, final String schemaUri) {
        final var organisationSchemaRecord = organisationSchemaRepository.findByUri(schemaUri)
                .orElseThrow(() -> new RuntimeException("Organisation schema not found %s".formatted(schemaUri)));

        final var organisationRecord = organisationRecordFactory.create(pid, organisationSchemaRecord.getId());

        final var organisation = organisationRepository.findOrCreate(organisationRecord);

        return organisation.getId();
    }
}
