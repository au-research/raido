package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.AccessTypeSchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.AccessTypeScheme.ACCESS_TYPE_SCHEME;

@Repository
public class AccessTypeSchemeRepository {
  private final DSLContext dslContext;

  public AccessTypeSchemeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<AccessTypeSchemeRecord> findByUri(final String uri) {
    return dslContext.select(ACCESS_TYPE_SCHEME.fields())
      .from(ACCESS_TYPE_SCHEME)
      .where(ACCESS_TYPE_SCHEME.URI.eq(uri))
      .fetchOptional(record -> new AccessTypeSchemeRecord()
        .setId(ACCESS_TYPE_SCHEME.ID.getValue(record))
        .setUri(ACCESS_TYPE_SCHEME.URI.getValue(record))
      );
  }
}
