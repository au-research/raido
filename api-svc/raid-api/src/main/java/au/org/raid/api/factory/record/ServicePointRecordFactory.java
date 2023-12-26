package au.org.raid.api.factory.record;

import au.org.raid.api.dto.ServicePointDto;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import org.springframework.stereotype.Component;

@Component
public class ServicePointRecordFactory {
    public ServicePointRecord create(final ServicePointDto servicePointDto) {
        return new ServicePointRecord()
                .setId(servicePointDto.getId())
                .setName(servicePointDto.getName())
                .setEnabled(servicePointDto.isEnabled())
                .setIdentifierOwner(servicePointDto.getIdentifier())
                .setAppWritesEnabled(servicePointDto.isAppWritesEnabled())
                .setAdminEmail(servicePointDto.getAdminEmail())
                .setTechEmail(servicePointDto.getTechEmail())
                .setEnabled(servicePointDto.isEnabled());
    }
}
