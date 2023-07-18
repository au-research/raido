package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.ContributorPositionTypeSchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.ContributorPositionTypeScheme.CONTRIBUTOR_POSITION_TYPE_SCHEME;

@Repository
public class ContributorPositionTypeSchemeRepository {
  private final DSLContext dslContext;

  public ContributorPositionTypeSchemeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<ContributorPositionTypeSchemeRecord> findByUri(final String uri) {
    return dslContext.select(CONTRIBUTOR_POSITION_TYPE_SCHEME.fields())
      .from(CONTRIBUTOR_POSITION_TYPE_SCHEME)
      .where(CONTRIBUTOR_POSITION_TYPE_SCHEME.URI.eq(uri))
      .fetchOptional(record -> new ContributorPositionTypeSchemeRecord()
        .setId(CONTRIBUTOR_POSITION_TYPE_SCHEME.ID.getValue(record))
        .setUri(CONTRIBUTOR_POSITION_TYPE_SCHEME.URI.getValue(record))
      );
  }
}
