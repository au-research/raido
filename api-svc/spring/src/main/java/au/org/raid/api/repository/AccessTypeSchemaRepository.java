package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.AccessTypeSchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.AccessTypeScheme.ACCESS_TYPE_SCHEME;

@Repository
@RequiredArgsConstructor
public class AccessTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<AccessTypeSchemeRecord> findByUri(final String uri) {
        return dslContext.select(ACCESS_TYPE_SCHEME.fields())
                .from(ACCESS_TYPE_SCHEME)
                .where(ACCESS_TYPE_SCHEME.URI.eq(uri))
                .fetchOptional(record -> new AccessTypeSchemeRecord()
                        .setId(ACCESS_TYPE_SCHEME.ID.getValue(record))
                        .setUri(ACCESS_TYPE_SCHEME.URI.getValue(record))
                );
    }
}