package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.ContributorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.Contributor.CONTRIBUTOR;

@Repository
@RequiredArgsConstructor
public class ContributorRepository {
    private final DSLContext dslContext;
    public ContributorRecord create(final ContributorRecord contributor) {
        return dslContext.insertInto(CONTRIBUTOR)
                .set(CONTRIBUTOR.PID, contributor.getPid())
                .set(CONTRIBUTOR.SCHEMA_ID, contributor.getSchemaId())
                .returning()
                .fetchOne();
    }

    public ContributorRecord findOrCreate(final ContributorRecord contributor) {
        final var result = dslContext.select(CONTRIBUTOR.fields())
                .from(CONTRIBUTOR)
                .where(CONTRIBUTOR.PID.eq(contributor.getPid())
                        .and(CONTRIBUTOR.SCHEMA_ID.eq(contributor.getSchemaId())))
                .fetchOptional(record -> new ContributorRecord()
                        .setId(CONTRIBUTOR.ID.getValue(record))
                        .setPid(CONTRIBUTOR.PID.getValue(record))
                        .setSchemaId(CONTRIBUTOR.SCHEMA_ID.getValue(record))
                );

        return result.orElseGet(() -> create(contributor));
    }
}
