package au.org.raid.api.factory;

import au.org.raid.api.service.raid.MetadataService;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.db.jooq.api_svc.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.Id;
import au.org.raid.idl.raidv2.model.IdBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdFactory {
    private final MetadataProps metaProps;

    public Id create(final IdentifierUrl id,
                     final ServicePointRecord servicePointRecord
    ) {
        return new Id().
                identifier(id.formatUrl()).
                identifierSchemeUri(MetadataService.RAID_ID_TYPE_URI).
                identifierRegistrationAgency(metaProps.identifierRegistrationAgency).
                identifierOwner(servicePointRecord.getIdentifierOwner()).
                identifierServicePoint(servicePointRecord.getId()).
                globalUrl(id.handle().format(metaProps.globalUrlPrefix)).
                raidAgencyUrl(id.handle().format(metaProps.handleUrlPrefix)).
                version(1);
    }

    public Id create(final IdBlock idBlock) {
        if (idBlock == null) {
            return null;
        }
        return new Id()
                .identifier(idBlock.getIdentifier())
                .identifierSchemeUri(idBlock.getIdentifierSchemeURI())
                .identifierRegistrationAgency(idBlock.getIdentifierRegistrationAgency())
                .identifierOwner(idBlock.getIdentifierOwner())
                .identifierServicePoint(idBlock.getIdentifierServicePoint())
                .globalUrl(idBlock.getGlobalUrl())
                .raidAgencyUrl(idBlock.getRaidAgencyUrl())
                .version(idBlock.getVersion());
    }
}