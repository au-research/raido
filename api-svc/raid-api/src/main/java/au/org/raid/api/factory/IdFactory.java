package au.org.raid.api.factory;

import au.org.raid.api.spring.config.IdentifierProperties;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.Id;
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
                .schemaUri(identifierProperties.getSchemaUri())
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
}