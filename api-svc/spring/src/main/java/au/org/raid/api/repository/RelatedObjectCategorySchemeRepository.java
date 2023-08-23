package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.RelatedObjectCategorySchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.RelatedObjectCategoryScheme.RELATED_OBJECT_CATEGORY_SCHEME;

@Repository
@RequiredArgsConstructor
public class RelatedObjectCategorySchemeRepository {
  private final DSLContext dslContext;

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