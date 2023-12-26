package au.org.raid.api.factory;

import au.org.raid.api.dto.ServicePointDto;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import org.springframework.stereotype.Component;

@Component
public class ServicePointDtoFactory {
    public ServicePointDto create(final ServicePointRecord servicePointRecord) {
        return ServicePointDto.builder()
                .id(servicePointRecord.getId())
                .name(servicePointRecord.getName())
                .adminEmail(servicePointRecord.getAdminEmail())
                .appWritesEnabled(servicePointRecord.getAppWritesEnabled())
                .enabled(servicePointRecord.getEnabled())
                .identifier(servicePointRecord.getIdentifierOwner())
                .techEmail(servicePointRecord.getTechEmail())
                .build();
    }
}
