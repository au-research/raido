package raido.apisvc.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import raido.db.jooq.api_svc.tables.records.SubjectRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.Subject.SUBJECT;

@Repository
@RequiredArgsConstructor
public class SubjectRepository {
  private final DSLContext dslContext;

  public Optional<SubjectRecord> findById(final String subjectId) {
    return dslContext.select(SUBJECT.fields()).
      from(SUBJECT).
      where(SUBJECT.ID.eq(subjectId)).
      fetchOptional(record -> new SubjectRecord()
        .setId(SUBJECT.ID.getValue(record))
        .setName(SUBJECT.NAME.getValue(record))
        .setDescription(SUBJECT.DESCRIPTION.getValue(record))
        .setNote(SUBJECT.NOTE.getValue(record))
      );
  }
}