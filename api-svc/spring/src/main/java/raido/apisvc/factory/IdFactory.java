package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.Id;

import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;

@Component
public class IdFactory {
  private final MetadataProps metaProps;

  public IdFactory(final MetadataProps metaProps) {
    this.metaProps = metaProps;
  }

  public Id create(final IdentifierUrl id,
                   final ServicePointRecord servicePointRecord
  ) {
    return new Id().
      identifier(id.formatUrl()).
      identifierSchemeURI(RAID_ID_TYPE_URI).
      identifierRegistrationAgency(metaProps.identifierRegistrationAgency).
      identifierOwner(servicePointRecord.getIdentifierOwner()).
      identifierServicePoint(servicePointRecord.getId()).
      globalUrl(id.handle().format(metaProps.globalUrlPrefix)).
      raidAgencyUrl(id.handle().format(metaProps.handleUrlPrefix)).
      version(1);
  }
}