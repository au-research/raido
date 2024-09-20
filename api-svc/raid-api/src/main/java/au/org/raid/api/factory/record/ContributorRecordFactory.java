package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.ContributorRecord;
import au.org.raid.idl.raidv2.model.Contributor;
import org.springframework.stereotype.Component;

@Component
public class ContributorRecordFactory {
    public ContributorRecord create(final Contributor contributor, final int schemaId) {
        return new ContributorRecord()
                .setUuid(contributor.getUuid())
                .setStatus(contributor.getStatus())
                .setPid(contributor.getId())
                .setSchemaId(schemaId);
    }
}
