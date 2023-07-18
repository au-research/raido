package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.ContributorRoleTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.ContributorRoleType.CONTRIBUTOR_ROLE_TYPE;

@Repository
public class ContributorRoleTypeRepository {
  private final DSLContext dslContext;

  public ContributorRoleTypeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<ContributorRoleTypeRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(CONTRIBUTOR_ROLE_TYPE.fields())
      .from(CONTRIBUTOR_ROLE_TYPE)
      .where(CONTRIBUTOR_ROLE_TYPE.URI.eq(uri)
        .and(CONTRIBUTOR_ROLE_TYPE.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new ContributorRoleTypeRecord()
        .setSchemeId(CONTRIBUTOR_ROLE_TYPE.SCHEME_ID.getValue(record))
        .setUri(CONTRIBUTOR_ROLE_TYPE.URI.getValue(record))
      );
  }
}
