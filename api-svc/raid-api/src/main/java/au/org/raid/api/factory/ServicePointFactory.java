package au.org.raid.api.factory;

import au.org.raid.api.dto.ServicePointDto.ServicePointDto;
import au.org.raid.idl.raidv2.model.ServicePoint;
import org.springframework.stereotype.Component;

@Component
public class ServicePointFactory {
    public ServicePoint create(final ServicePointDto servicePointDto) {
        return new ServicePoint()
                .id(servicePointDto.getId())
                .name(servicePointDto.getName())
                .adminEmail(servicePointDto.getAdminEmail())
                .techEmail(servicePointDto.getTechEmail())
                .enabled(servicePointDto.isEnabled())
                .appWritesEnabled(servicePointDto.isAppWritesEnabled());

    }
}
