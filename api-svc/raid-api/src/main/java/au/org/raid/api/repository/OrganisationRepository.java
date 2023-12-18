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
        final var result = dslContext.selectFrom(ORGANISATION)
                .where(ORGANISATION.PID.eq(record.getPid()))
                .and(ORGANISATION.SCHEMA_ID.eq(record.getSchemaId()))
                .fetchOptional();

        return result.orElseGet(() -> create(record));
    }

    public Optional<OrganisationRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.selectFrom(ORGANISATION)
                .where(ORGANISATION.PID.eq(uri)).and(ORGANISATION.SCHEMA_ID.eq(schemaId))
                .fetchOptional();
    }

    public Optional<OrganisationRecord> findById(final Integer id) {
        return dslContext.selectFrom(ORGANISATION)
                .where(ORGANISATION.ID.eq(id))
                .fetchOptional();
    }
}
