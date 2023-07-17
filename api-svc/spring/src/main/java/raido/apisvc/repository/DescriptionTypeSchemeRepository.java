package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeSchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.DescriptionTypeScheme.DESCRIPTION_TYPE_SCHEME;

@Repository
public class DescriptionTypeSchemeRepository {
  private final DSLContext dslContext;

  public DescriptionTypeSchemeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<DescriptionTypeSchemeRecord> findByUri(final String uri) {
    return dslContext.select(DESCRIPTION_TYPE_SCHEME.fields()).
      from(DESCRIPTION_TYPE_SCHEME).
      where(DESCRIPTION_TYPE_SCHEME.URI.eq(uri)).
      fetchOptional(record -> new DescriptionTypeSchemeRecord()
        .setId(DESCRIPTION_TYPE_SCHEME.ID.getValue(record))
        .setUri(DESCRIPTION_TYPE_SCHEME.URI.getValue(record))
      );
  }
}
