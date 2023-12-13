package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.OrganisationRecord;
import au.org.raid.idl.raidv2.model.Organisation;
import org.springframework.stereotype.Component;

@Component
public class OrganisationRecordFactory {
    public OrganisationRecord create(final Organisation organisation, final int schemaId) {
        return create(organisation.getId(), schemaId);
    }

    public OrganisationRecord create(final String pid, final Integer schemaId) {
        return new OrganisationRecord()
                .setPid(pid)
                .setSchemaId(schemaId);
    }
}
