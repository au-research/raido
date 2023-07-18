package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.ContributorPositionTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.ContributorPositionType.CONTRIBUTOR_POSITION_TYPE;

@Repository
public class ContributorPositionTypeRepository {
  private final DSLContext dslContext;

  public ContributorPositionTypeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<ContributorPositionTypeRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(CONTRIBUTOR_POSITION_TYPE.fields())
      .from(CONTRIBUTOR_POSITION_TYPE)
      .where(CONTRIBUTOR_POSITION_TYPE.URI.eq(uri)
        .and(CONTRIBUTOR_POSITION_TYPE.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new ContributorPositionTypeRecord()
        .setSchemeId(CONTRIBUTOR_POSITION_TYPE.SCHEME_ID.getValue(record))
        .setUri(CONTRIBUTOR_POSITION_TYPE.URI.getValue(record))
      );
  }
}
