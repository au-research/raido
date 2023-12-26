package au.org.raid.api.factory;

import au.org.raid.api.service.raid.MetadataService;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.config.IdentifierProperties;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.Id;
import au.org.raid.idl.raidv2.model.IdBlock;
import au.org.raid.idl.raidv2.model.Owner;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdFactory {
    private final IdentifierProperties identifierProperties;

    public Id create(final String handle,
                     final ServicePointRecord servicePointRecord
    ) {
        return new Id().
                id(String.format("%s%s", identifierProperties.getNamePrefix(), handle))
                .schemaUri(MetadataService.RAID_ID_TYPE_URI)
                .registrationAgency(new RegistrationAgency()
                        .id(identifierProperties.getRegistrationAgencyIdentifier())
                        .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())
                )
                .owner(new Owner()
                        .id(servicePointRecord.getIdentifierOwner())
                        .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())
                        .servicePoint(servicePointRecord.getId())
                )
                .globalUrl(String.format("%s%s", identifierProperties.getGlobalUrlPrefix(), handle))
                .raidAgencyUrl(String.format("%s%s", identifierProperties.getHandleUrlPrefix(), handle))
                .license(identifierProperties.getLicense())
                .version(1);
    }

    public Id create(final IdBlock idBlock) {
        if (idBlock == null) {
            return null;
        }
        return new Id()
                .id(idBlock.getIdentifier())
                .schemaUri(idBlock.getIdentifierSchemeURI())
                .registrationAgency(new RegistrationAgency()
                        .id(idBlock.getIdentifierRegistrationAgency())
                        .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())
                )
                .owner(new Owner()
                        .id(idBlock.getIdentifierOwner())
                        .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())
                        .servicePoint(idBlock.getIdentifierServicePoint())
                )
                .globalUrl(idBlock.getGlobalUrl())
                .raidAgencyUrl(idBlock.getRaidAgencyUrl())
                .license(identifierProperties.getLicense())
                .version(idBlock.getVersion());
    }
}