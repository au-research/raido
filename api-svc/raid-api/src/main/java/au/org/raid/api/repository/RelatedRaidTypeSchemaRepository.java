package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedRaidTypeSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedRaidTypeSchema.RELATED_RAID_TYPE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class RelatedRaidTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<RelatedRaidTypeSchemaRecord> findByUri(final String uri) {
        return dslContext.select(RELATED_RAID_TYPE_SCHEMA.fields())
                .from(RELATED_RAID_TYPE_SCHEMA)
                .where(RELATED_RAID_TYPE_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new RelatedRaidTypeSchemaRecord()
                        .setId(RELATED_RAID_TYPE_SCHEMA.ID.getValue(record))
                        .setUri(RELATED_RAID_TYPE_SCHEMA.URI.getValue(record))
                );
    }
}