package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidSpatialCoverageRecord;
import au.org.raid.idl.raidv2.model.SpatialCoverage;
import org.springframework.stereotype.Component;

@Component
public class RaidSpatialCoverageRecordFactory {
    public RaidSpatialCoverageRecord create(final String uri, final String handle, final Integer schemaId) {
        return new RaidSpatialCoverageRecord()
                .setHandle(handle)
                .setSchemaId(schemaId)
                .setUri(uri);
    }
}
