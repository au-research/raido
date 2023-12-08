package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedObjectRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RelatedObject.RELATED_OBJECT;

@Repository
@RequiredArgsConstructor
public class RelatedObjectRepository {
    private final DSLContext dslContext;

    public RelatedObjectRecord create(final RelatedObjectRecord record) {
        return dslContext.insertInto(RELATED_OBJECT)
                .set(RELATED_OBJECT.PID, record.getPid())
                .set(RELATED_OBJECT.SCHEMA_ID, record.getSchemaId())
                .returning()
                .fetchOne();
    }
}
