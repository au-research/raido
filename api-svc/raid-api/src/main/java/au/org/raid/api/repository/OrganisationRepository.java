package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.OrganisationRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.Organisation.ORGANISATION;

@Repository
@RequiredArgsConstructor
public class OrganisationRepository {
    private final DSLContext dslContext;
    public OrganisationRecord create(final OrganisationRecord record) {
        return dslContext.insertInto(ORGANISATION)
                .set(ORGANISATION.PID, record.getPid())
                .set(ORGANISATION.SCHEMA_ID, record.getSchemaId())
                .onConflictDoNothing()
                .returning()
                .fetchOne();
    }

    public OrganisationRecord findOrCreate(final OrganisationRecord record) {
        final var result = dslContext.select(ORGANISATION.fields())
                .from(ORGANISATION)
                .where(ORGANISATION.PID.eq(record.getPid()))
                .and(ORGANISATION.SCHEMA_ID.eq(record.getSchemaId()))
                .fetchOptional(r -> new OrganisationRecord()
                        .setId(ORGANISATION.ID.getValue(r))
                        .setPid(ORGANISATION.PID.getValue(r))
                        .setSchemaId(ORGANISATION.SCHEMA_ID.getValue(r))
                );

        return result.orElseGet(() -> create(record));
    }

    public Optional<OrganisationRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(ORGANISATION.fields())
                .from(ORGANISATION)
                .where(ORGANISATION.PID.eq(uri)).and(ORGANISATION.SCHEMA_ID.eq(schemaId))
                .fetchOptional(record -> new OrganisationRecord()
                        .setId(ORGANISATION.ID.getValue(record))
                        .setPid(ORGANISATION.PID.getValue(record))
                        .setSchemaId(ORGANISATION.SCHEMA_ID.getValue(record)));
    }
}
