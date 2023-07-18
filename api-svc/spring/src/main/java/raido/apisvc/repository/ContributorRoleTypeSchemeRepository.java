package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.ContributorRoleTypeSchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.ContributorRoleTypeScheme.CONTRIBUTOR_ROLE_TYPE_SCHEME;

@Repository
public class ContributorRoleTypeSchemeRepository {
  private final DSLContext dslContext;

  public ContributorRoleTypeSchemeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<ContributorRoleTypeSchemeRecord> findByUri(final String uri) {
    return dslContext.select(CONTRIBUTOR_ROLE_TYPE_SCHEME.fields())
      .from(CONTRIBUTOR_ROLE_TYPE_SCHEME)
      .where(CONTRIBUTOR_ROLE_TYPE_SCHEME.URI.eq(uri))
      .fetchOptional(record -> new ContributorRoleTypeSchemeRecord()
        .setId(CONTRIBUTOR_ROLE_TYPE_SCHEME.ID.getValue(record))
        .setUri(CONTRIBUTOR_ROLE_TYPE_SCHEME.URI.getValue(record))
      );
  }
}
