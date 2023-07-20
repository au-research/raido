package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategorySchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.RelatedObjectCategoryScheme.RELATED_OBJECT_CATEGORY_SCHEME;

@Repository
public class RelatedObjectCategorySchemeRepository {
  private final DSLContext dslContext;

  public RelatedObjectCategorySchemeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<RelatedObjectCategorySchemeRecord> findByUri(final String uri) {
    return dslContext.select(RELATED_OBJECT_CATEGORY_SCHEME.fields())
      .from(RELATED_OBJECT_CATEGORY_SCHEME)
      .where(RELATED_OBJECT_CATEGORY_SCHEME.URI.eq(uri))
      .fetchOptional(record -> new RelatedObjectCategorySchemeRecord()
        .setId(RELATED_OBJECT_CATEGORY_SCHEME.ID.getValue(record))
        .setUri(RELATED_OBJECT_CATEGORY_SCHEME.URI.getValue(record))
      );
  }
}
