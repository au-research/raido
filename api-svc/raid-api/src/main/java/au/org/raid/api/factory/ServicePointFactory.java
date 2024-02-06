package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.ServicePoint;
import org.springframework.stereotype.Component;

@Component
public class ServicePointFactory {
    public ServicePoint create(final ServicePoint servicePointDto) {
        return new ServicePoint()
                .id(servicePointDto.getId())
                .name(servicePointDto.getName())
                .adminEmail(servicePointDto.getAdminEmail())
                .techEmail(servicePointDto.getTechEmail())
                .repositoryId(servicePointDto.getRepositoryId())
                .prefix(servicePointDto.getPrefix())
                .password(servicePointDto.getPassword())
                .enabled(servicePointDto.getEnabled())
                .appWritesEnabled(servicePointDto.getAppWritesEnabled());
    }

    public ServicePoint create(final ServicePointRecord servicePointRecord) {
        return new ServicePoint()
                .id(servicePointRecord.getId())
                .name(servicePointRecord.getName())
                .adminEmail(servicePointRecord.getAdminEmail())
                .appWritesEnabled(servicePointRecord.getAppWritesEnabled())
                .enabled(servicePointRecord.getEnabled())
                .identifierOwner(servicePointRecord.getIdentifierOwner())
                .techEmail(servicePointRecord.getTechEmail())
                .repositoryId(servicePointRecord.getRepositoryId())
                .prefix(servicePointRecord.getPrefix())
                .password(servicePointRecord.getPassword());
    }
}
