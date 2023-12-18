package au.org.raid.api.service;

import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.idl.raidv2.model.Id;
import au.org.raid.idl.raidv2.model.Owner;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class IdService {

    private final OrganisationService organisationService;

    public Id getId(final RaidRecord record) {
        final var registrationAgencyUri = organisationService.findOrganisationUri(record.getRegistrationAgencyOrganisationId());
        final var registrationAgencySchemaUri = organisationService.findOrganisationSchemaUri(record.getRegistrationAgencyOrganisationId());
        final var ownerUri = organisationService.findOrganisationUri(record.getOwnerOrganisationId());
        final var ownerSchemaUri = organisationService.findOrganisationSchemaUri(record.getOwnerOrganisationId());

        return new Id()
                .id(record.getHandle())
                .schemaUri(record.getSchemaUri())
                .registrationAgency(new RegistrationAgency()
                        .id(registrationAgencyUri)
                        .schemaUri(registrationAgencySchemaUri)
                )
                .owner(new Owner()
                        .id(ownerUri)
                        .schemaUri(ownerSchemaUri)
                        .servicePoint(record.getServicePointId())
                )
                .license(record.getLicense())
                .version(record.getVersion());


    }
}
