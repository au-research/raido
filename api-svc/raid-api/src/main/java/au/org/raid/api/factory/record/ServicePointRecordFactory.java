package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.ServicePointCreateRequest;
import au.org.raid.idl.raidv2.model.ServicePointUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class ServicePointRecordFactory {
    public ServicePointRecord create(final ServicePointUpdateRequest servicePoint) {
        return new ServicePointRecord()
                .setId(servicePoint.getId())
                .setName(servicePoint.getName())
                .setIdentifierOwner(servicePoint.getIdentifierOwner())
                .setAdminEmail(servicePoint.getAdminEmail())
                .setTechEmail(servicePoint.getTechEmail())
                .setAppWritesEnabled(servicePoint.getAppWritesEnabled())
                .setEnabled(servicePoint.getEnabled())
                .setRepositoryId(servicePoint.getRepositoryId())
                .setPrefix(servicePoint.getPrefix())
                .setPassword(servicePoint.getPassword())
                .setGroupId(servicePoint.getGroupId());
    }

    public ServicePointRecord create(final ServicePointCreateRequest servicePoint) {
        return new ServicePointRecord()
                .setName(servicePoint.getName())
                .setIdentifierOwner(servicePoint.getIdentifierOwner())
                .setAdminEmail(servicePoint.getAdminEmail())
                .setTechEmail(servicePoint.getTechEmail())
                .setAppWritesEnabled(servicePoint.getAppWritesEnabled())
                .setEnabled(servicePoint.getEnabled())
                .setRepositoryId(servicePoint.getRepositoryId())
                .setPrefix(servicePoint.getPrefix())
                .setPassword(servicePoint.getPassword())
                .setGroupId(servicePoint.getGroupId());
    }
}
