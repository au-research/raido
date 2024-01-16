package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.SubjectTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.SubjectType.SUBJECT_TYPE;

@Repository
@RequiredArgsConstructor
public class SubjectTypeRepository {
    private final DSLContext dslContext;

    public Optional<SubjectTypeRecord> findById(final String subjectId) {
        return dslContext.selectFrom(SUBJECT_TYPE)
                .where(SUBJECT_TYPE.ID.eq(subjectId))
                .fetchOptional();
    }
}