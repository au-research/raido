package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.TitleTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.TitleType.TITLE_TYPE;

@Repository
public class TitleTypeRepository {
  private final DSLContext dslContext;

  public TitleTypeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<TitleTypeRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(TITLE_TYPE.fields())
      .from(TITLE_TYPE)
      .where(TITLE_TYPE.URI.eq(uri)
        .and(TITLE_TYPE.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new TitleTypeRecord()
        .setSchemeId(TITLE_TYPE.SCHEME_ID.getValue(record))
        .setUri(TITLE_TYPE.URI.getValue(record))
      );
  }
}
