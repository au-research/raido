package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.SubjectTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.SubjectType.SUBJECT_TYPE;

@Repository
@RequiredArgsConstructor
public class SubjectTypeRepository {
    private final DSLContext dslContext;

    public Optional<SubjectTypeRecord> findById(final String subjectId) {
        return dslContext.select(SUBJECT_TYPE.fields()).
                from(SUBJECT_TYPE).
                where(SUBJECT_TYPE.ID.eq(subjectId)).
                fetchOptional(record -> new SubjectTypeRecord()
                        .setId(SUBJECT_TYPE.ID.getValue(record))
                        .setName(SUBJECT_TYPE.NAME.getValue(record))
                        .setDescription(SUBJECT_TYPE.DESCRIPTION.getValue(record))
                        .setNote(SUBJECT_TYPE.NOTE.getValue(record))
                );
    }
}