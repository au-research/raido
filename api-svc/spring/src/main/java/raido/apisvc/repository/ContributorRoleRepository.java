package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.ContributorRoleRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.ContributorRole.CONTRIBUTOR_ROLE;

@Repository
public class ContributorRoleRepository {
  private final DSLContext dslContext;

  public ContributorRoleRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<ContributorRoleRecord> findByUriAndSchemeId(final String uri, final int schemeId) {
    return dslContext.select(CONTRIBUTOR_ROLE.fields())
      .from(CONTRIBUTOR_ROLE)
      .where(CONTRIBUTOR_ROLE.URI.eq(uri)
        .and(CONTRIBUTOR_ROLE.SCHEME_ID.eq(schemeId)))
      .fetchOptional(record -> new ContributorRoleRecord()
        .setSchemeId(CONTRIBUTOR_ROLE.SCHEME_ID.getValue(record))
        .setUri(CONTRIBUTOR_ROLE.URI.getValue(record))
      );
  }
}
