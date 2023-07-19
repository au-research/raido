package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.ContributorPositionRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.ContributorPosition.CONTRIBUTOR_POSITION;

@Repository
public class ContributorPositionRepository {
  private final DSLContext dslContext;

  public ContributorPositionRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<ContributorPositionRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(CONTRIBUTOR_POSITION.fields())
      .from(CONTRIBUTOR_POSITION)
      .where(CONTRIBUTOR_POSITION.URI.eq(uri)
        .and(CONTRIBUTOR_POSITION.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new ContributorPositionRecord()
        .setSchemeId(CONTRIBUTOR_POSITION.SCHEME_ID.getValue(record))
        .setUri(CONTRIBUTOR_POSITION.URI.getValue(record))
      );
  }
}
