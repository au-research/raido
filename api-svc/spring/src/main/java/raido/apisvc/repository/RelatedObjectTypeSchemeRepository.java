package raido.apisvc.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.RelatedObjectTypeSchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.RelatedObjectTypeScheme.RELATED_OBJECT_TYPE_SCHEME;

@Repository
@RequiredArgsConstructor
public class RelatedObjectTypeSchemeRepository {
  private final DSLContext dslContext;

  public Optional<RelatedObjectTypeSchemeRecord> findByUri(final String uri) {
    return dslContext.select(RELATED_OBJECT_TYPE_SCHEME.fields())
      .from(RELATED_OBJECT_TYPE_SCHEME)
      .where(RELATED_OBJECT_TYPE_SCHEME.URI.eq(uri))
      .fetchOptional(record -> new RelatedObjectTypeSchemeRecord()
        .setId(RELATED_OBJECT_TYPE_SCHEME.ID.getValue(record))
        .setUri(RELATED_OBJECT_TYPE_SCHEME.URI.getValue(record))
      );
  }
}