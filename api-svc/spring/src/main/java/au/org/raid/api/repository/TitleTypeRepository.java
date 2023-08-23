package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.TitleTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.TitleType.TITLE_TYPE;

@Repository
@RequiredArgsConstructor
public class TitleTypeRepository {
  private final DSLContext dslContext;

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