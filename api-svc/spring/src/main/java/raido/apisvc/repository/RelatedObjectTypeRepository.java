package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.RelatedObjectTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.RelatedObjectType.RELATED_OBJECT_TYPE;

@Repository
public class RelatedObjectTypeRepository {
  private final DSLContext dslContext;

  public RelatedObjectTypeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<RelatedObjectTypeRecord> findByUrl(final String url) {
    return dslContext.select(RELATED_OBJECT_TYPE.fields())
      .from(RELATED_OBJECT_TYPE)
      .where(RELATED_OBJECT_TYPE.URL.eq(url)).
      fetchOptional(record -> new RelatedObjectTypeRecord()
        .setName(RELATED_OBJECT_TYPE.NAME.getValue(record))
        .setDescription(RELATED_OBJECT_TYPE.DESCRIPTION.getValue(record))
        .setUrl(RELATED_OBJECT_TYPE.URL.getValue(record))
      );
  }
}
