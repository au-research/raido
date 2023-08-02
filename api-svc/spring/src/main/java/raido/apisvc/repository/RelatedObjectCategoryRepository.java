package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategoryRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.RelatedObjectCategory.RELATED_OBJECT_CATEGORY;

@Repository
public class RelatedObjectCategoryRepository {
  private final DSLContext dslContext;

  public RelatedObjectCategoryRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<RelatedObjectCategoryRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(RELATED_OBJECT_CATEGORY.fields())
      .from(RELATED_OBJECT_CATEGORY)
      .where(RELATED_OBJECT_CATEGORY.URI.eq(uri)
        .and(RELATED_OBJECT_CATEGORY.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new RelatedObjectCategoryRecord()
        .setSchemeId(RELATED_OBJECT_CATEGORY.SCHEME_ID.getValue(record))
        .setUri(RELATED_OBJECT_CATEGORY.URI.getValue(record))
      );
  }
}