package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedRaidTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedRaidType.RELATED_RAID_TYPE;

@Repository
@RequiredArgsConstructor
public class RelatedRaidTypeRepository {
    private final DSLContext dslContext;

    public Optional<RelatedRaidTypeRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(RELATED_RAID_TYPE.fields())
                .from(RELATED_RAID_TYPE)
                .where(RELATED_RAID_TYPE.URI.eq(uri).and(RELATED_RAID_TYPE.SCHEMA_ID.eq(schemaId))).
                fetchOptional(record -> new RelatedRaidTypeRecord()
                        .setSchemaId(RELATED_RAID_TYPE.SCHEMA_ID.getValue(record))
                        .setUri(RELATED_RAID_TYPE.URI.getValue(record))
                        .setName(RELATED_RAID_TYPE.NAME.getValue(record))
                        .setDescription(RELATED_RAID_TYPE.DESCRIPTION.getValue(record))
                );
    }

    public Optional<RelatedRaidTypeRecord> findByUri(final String uri) {
        return dslContext.select(RELATED_RAID_TYPE.fields())
                .from(RELATED_RAID_TYPE)
                .where(RELATED_RAID_TYPE.URI.eq(uri)).
                fetchOptional(record -> new RelatedRaidTypeRecord()
                        .setSchemaId(RELATED_RAID_TYPE.SCHEMA_ID.getValue(record))
                        .setUri(RELATED_RAID_TYPE.URI.getValue(record))
                        .setName(RELATED_RAID_TYPE.NAME.getValue(record))
                        .setDescription(RELATED_RAID_TYPE.DESCRIPTION.getValue(record))
                );
    }
}