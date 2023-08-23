package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.RelatedRaidTypeSchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.RelatedRaidTypeScheme.RELATED_RAID_TYPE_SCHEME;

@Repository
@RequiredArgsConstructor
public class RelatedRaidTypeSchemeRepository {
    private final DSLContext dslContext;

    public Optional<RelatedRaidTypeSchemeRecord> findByUri(final String uri) {
        return dslContext.select(RELATED_RAID_TYPE_SCHEME.fields())
                .from(RELATED_RAID_TYPE_SCHEME)
                .where(RELATED_RAID_TYPE_SCHEME.URI.eq(uri))
                .fetchOptional(record -> new RelatedRaidTypeSchemeRecord()
                        .setId(RELATED_RAID_TYPE_SCHEME.ID.getValue(record))
                        .setUri(RELATED_RAID_TYPE_SCHEME.URI.getValue(record))
                );
    }
}