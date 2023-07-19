package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.OrganisationRoleSchemeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.OrganisationRoleScheme.ORGANISATION_ROLE_SCHEME;

@Repository
public class OrganisationRoleSchemeRepository {
  private final DSLContext dslContext;

  public OrganisationRoleSchemeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<OrganisationRoleSchemeRecord> findByUri(final String uri) {
    return dslContext.select(ORGANISATION_ROLE_SCHEME.fields())
      .from(ORGANISATION_ROLE_SCHEME)
      .where(ORGANISATION_ROLE_SCHEME.URI.eq(uri))
      .fetchOptional(record -> new OrganisationRoleSchemeRecord()
        .setId(ORGANISATION_ROLE_SCHEME.ID.getValue(record))
        .setUri(ORGANISATION_ROLE_SCHEME.URI.getValue(record))
      );
  }
}
