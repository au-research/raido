package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidSpatialCoveragePlaceRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidSpatialCoveragePlaceRecordFactory {
    public RaidSpatialCoveragePlaceRecord create(
            final Integer raidSpatialCoverageId,
            final String place,
            final Integer languageId) {

        return new RaidSpatialCoveragePlaceRecord()
                .setRaidSpatialCoverageId(raidSpatialCoverageId)
                .setPlace(place)
                .setLanguageId(languageId);
    }
}
