package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.AccessTypeSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.AccessTypeSchema.ACCESS_TYPE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class AccessTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<AccessTypeSchemaRecord> findByUri(final String uri) {
        return dslContext.selectFrom(ACCESS_TYPE_SCHEMA)
                .where(ACCESS_TYPE_SCHEMA.URI.eq(uri))
                .fetchOptional();
    }

    public Optional<AccessTypeSchemaRecord> findById(final Integer id) {
        return dslContext.selectFrom(ACCESS_TYPE_SCHEMA)
                .where(ACCESS_TYPE_SCHEMA.ID.eq(id))
                .fetchOptional();
    }
}