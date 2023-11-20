package au.org.raid.api.repository.dto;

import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;

public record Raid(
        RaidRecord raid,
        ServicePointRecord servicePoint
) {
    @Override
    public RaidRecord raid() {
        return raid;
    }

    @Override
    public ServicePointRecord servicePoint() {
        return servicePoint;
    }
}
