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

  public Optional<RelatedObjectTypeRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(RELATED_OBJECT_TYPE.fields())
      .from(RELATED_OBJECT_TYPE)
      .where(RELATED_OBJECT_TYPE.URI.eq(uri).and(RELATED_OBJECT_TYPE.SCHEME_ID.eq(schemeId))).
      fetchOptional(record -> new RelatedObjectTypeRecord()
        .setSchemeId(RELATED_OBJECT_TYPE.SCHEME_ID.getValue(record))
        .setUri(RELATED_OBJECT_TYPE.URI.getValue(record))
        .setName(RELATED_OBJECT_TYPE.NAME.getValue(record))
        .setDescription(RELATED_OBJECT_TYPE.DESCRIPTION.getValue(record))
      );
  }
}
