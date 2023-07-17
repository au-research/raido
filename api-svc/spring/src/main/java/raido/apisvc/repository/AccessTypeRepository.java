package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.AccessTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.AccessType.ACCESS_TYPE;

@Repository
public class AccessTypeRepository {
  private final DSLContext dslContext;

  public AccessTypeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<AccessTypeRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(ACCESS_TYPE.fields())
      .from(ACCESS_TYPE)
      .where(ACCESS_TYPE.URI.eq(uri)
        .and(ACCESS_TYPE.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new AccessTypeRecord()
        .setSchemeId(ACCESS_TYPE.SCHEME_ID.getValue(record))
        .setUri(ACCESS_TYPE.URI.getValue(record))
      );
  }
}
