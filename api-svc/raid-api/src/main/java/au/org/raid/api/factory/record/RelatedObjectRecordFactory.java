package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RelatedObjectRecord;
import org.springframework.stereotype.Component;

@Component
public class RelatedObjectRecordFactory {
    public RelatedObjectRecord create(final String pid, final Integer schemaId) {
        return new RelatedObjectRecord()
                .setPid(pid)
                .setSchemaId(schemaId);
    }
}
