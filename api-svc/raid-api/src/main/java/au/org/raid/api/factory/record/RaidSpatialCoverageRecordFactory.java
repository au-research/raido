package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidSpatialCoverageRecord;
import au.org.raid.idl.raidv2.model.SpatialCoverage;
import org.springframework.stereotype.Component;

@Component
public class RaidSpatialCoverageRecordFactory {
    public RaidSpatialCoverageRecord create(final SpatialCoverage spatialCoverage, final String raidName, final Integer schemaId, final Integer languageId) {
        return new RaidSpatialCoverageRecord()
                .setRaidName(raidName)
                .setSchemaId(schemaId)
                .setLanguageId(languageId)
                .setPlace(spatialCoverage.getPlace())
                .setId(spatialCoverage.getId());
    }
}
